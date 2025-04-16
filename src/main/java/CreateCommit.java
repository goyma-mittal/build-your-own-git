import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;

public class CreateCommit {

    public static String createCommitObject(String treeSha, String parentSha, String message) {
        String author = "Goyma <goyma@example.com>";
        long timestamp = System.currentTimeMillis() / 1000;

        StringBuilder commitBuilder = new StringBuilder();
        commitBuilder.append("tree ").append(treeSha).append("\n");
        if (parentSha != null && !parentSha.isEmpty()) {
            commitBuilder.append("parent ").append(parentSha).append("\n");
        }
        commitBuilder.append("author ").append(author).append(" ").append(timestamp).append(" +0000\n");
        commitBuilder.append("committer ").append(author).append(" ").append(timestamp).append(" +0000\n");
        commitBuilder.append("\n").append(message).append("\n");

        String commitContent = commitBuilder.toString();
        String header = "commit " + commitContent.getBytes().length + "\0";

        byte[] storeData = concat(header.getBytes(), commitContent.getBytes());

        try {
            byte[] shaBytes = sha1(storeData);
            String sha = bytesToHex(shaBytes);

            String dir = ".git/objects/" + sha.substring(0, 2);
            String file = sha.substring(2);
            File path = new File(dir);
            if (!path.exists()) path.mkdirs();

            FileOutputStream out = new FileOutputStream(new File(path, file));
            DeflaterOutputStream deflater = new DeflaterOutputStream(out);
            deflater.write(storeData);
            deflater.close();

            return sha;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create commit object", e);
        }
    }

    public static byte[] sha1(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(input);
        } catch (Exception e) {
            throw new RuntimeException("SHA-1 digest failed", e);
        }
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
