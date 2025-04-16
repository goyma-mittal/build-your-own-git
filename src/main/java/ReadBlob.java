import java.io.*;
import java.util.zip.InflaterInputStream;

public class ReadBlob {
    public static void printBlob(String hash) {
        // Git stores objects in .git/objects/XX/YY... where XX is the first 2 characters
        String dir = hash.substring(0, 2);
        String file = hash.substring(2);
        File objectFile = new File(".git/objects/" + dir + "/" + file);

        if (!objectFile.exists()) {
            System.err.println("fatal: Not a valid object name " + hash);
            return;
        }

        try (
            FileInputStream fis = new FileInputStream(objectFile);
            InflaterInputStream zlibStream = new InflaterInputStream(fis);
            ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            // Decompress the zlib-compressed object
            zlibStream.transferTo(out);
            byte[] decompressed = out.toByteArray();

            // The blob header is like: "blob <size>\0"
            int nullIndex = 0;
            while (decompressed[nullIndex] != 0) {
                nullIndex++;
            }

            // Actual content starts after the null byte
            int contentStart = nullIndex + 1;
            byte[] content = new byte[decompressed.length - contentStart];
            System.arraycopy(decompressed, contentStart, content, 0, content.length);

            // Print the blob content
            System.out.write(content);
            System.out.flush();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read blob object", e);
        }
    }
}
