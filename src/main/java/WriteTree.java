import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.DeflaterOutputStream;

public class WriteTree {
    public static String writeTree(File dir) {
        List<TreeEntry> entries = new ArrayList<>();

        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file.getName().equals(".git")) continue;

            try {
                if (file.isFile()) {
                    byte[] content = Files.readAllBytes(file.toPath());

                    // Create blob object for the file
                    String header = "blob " + content.length + "\0";
                    byte[] blobData = concat(header.getBytes(), content);
                    String sha1 = sha1(blobData);
                    writeObject(sha1, blobData);

                    entries.add(new TreeEntry("100644", file.getName(), sha1));
                } else if (file.isDirectory()) {
                    String treeSha = writeTree(file);
                    entries.add(new TreeEntry("40000", file.getName(), treeSha));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Sort entries alphabetically by name
        entries.sort(Comparator.comparing(e -> e.name));

        ByteArrayOutputStream treeContent = new ByteArrayOutputStream();
        for (TreeEntry entry : entries) {
            try {
                treeContent.write((entry.mode + " " + entry.name).getBytes());
                treeContent.write(0); // null byte
                treeContent.write(hexToBytes(entry.sha));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        byte[] content = treeContent.toByteArray();
        String header = "tree " + content.length + "\0";
        byte[] treeData = concat(header.getBytes(), content);
        String treeSha = sha1(treeData);
        writeObject(treeSha, treeData);
        return treeSha;
    }

    public static void execute() {
        String sha = writeTree(new File("."));
        System.out.println(sha);
    }

    // SHA-1 hash of the byte array
    public static String sha1(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(input);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Write a Git object into the .git/objects directory
    public static void writeObject(String sha, byte[] data) {
        String dir = sha.substring(0, 2);
        String file = sha.substring(2);
        File objectDir = new File(".git/objects/" + dir);
        objectDir.mkdirs();

        File objectFile = new File(objectDir, file);
        if (objectFile.exists()) return;

        try (
            FileOutputStream fos = new FileOutputStream(objectFile);
            DeflaterOutputStream dos = new DeflaterOutputStream(fos)
        ) {
            dos.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Concatenate two byte arrays
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // Convert a SHA string to raw 20-byte format
    public static byte[] hexToBytes(String hex) {
        byte[] result = new byte[20];
        for (int i = 0; i < 20; i++) {
            int index = i * 2;
            result[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        }
        return result;
    }

    // Class to hold tree entry metadata
    public static class TreeEntry {
        public String mode, name, sha;

        public TreeEntry(String mode, String name, String sha) {
            this.mode = mode;
            this.name = name;
            this.sha = sha;
        }
    }
}
