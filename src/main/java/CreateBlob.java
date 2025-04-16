import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;

public class CreateBlob {
    public static void hashAndWrite(String filePath) {
        try {
            // Read file content
            byte[] content = Files.readAllBytes(new File(filePath).toPath());

            // Build blob header and combine with content
            String header = "blob " + content.length + "\0";
            byte[] blobData = concat(header.getBytes(), content);

            // Generate SHA-1 hash
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1.digest(blobData);
            String hash = bytesToHex(hashBytes);

            // Determine Git object file path
            String dir = hash.substring(0, 2);
            String file = hash.substring(2);
            File objectDir = new File(".git/objects/" + dir);
            File objectFile = new File(objectDir, file);

            // Only write object if it doesn't already exist
            if (!objectFile.exists()) {
                objectDir.mkdirs();
                try (
                    FileOutputStream fos = new FileOutputStream(objectFile);
                    DeflaterOutputStream deflater = new DeflaterOutputStream(fos)
                ) {
                    deflater.write(blobData);
                }
            }

            // Output the SHA-1 hash
            System.out.println(hash);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create blob", e);
        }
    }

    // Concatenate two byte arrays
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // Convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
