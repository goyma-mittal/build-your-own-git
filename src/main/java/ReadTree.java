import java.io.*;
import java.util.*;
import java.util.zip.InflaterInputStream;

public class ReadTree {
    public static void lsTree(String treeSha) {
        // Locate the object file based on SHA
        String dir = treeSha.substring(0, 2);
        String file = treeSha.substring(2);
        File objectFile = new File(".git/objects/" + dir + "/" + file);

        if (!objectFile.exists()) {
            System.err.println("fatal: Not a valid object name " + treeSha);
            return;
        }

        try (
            FileInputStream fis = new FileInputStream(objectFile);
            InflaterInputStream zis = new InflaterInputStream(fis)
        ) {
            byte[] objectData = zis.readAllBytes();
            int index = 0;

            // Skip the header "tree <size>\0"
            while (objectData[index] != 0) {
                index++;
            }
            index++; // move past null byte

            List<String> entries = new ArrayList<>();

            while (index < objectData.length) {
                // Read mode (e.g., "100644") — ends with space
                int modeStart = index;
                while (objectData[index] != ' ') {
                    index++;
                }
                String mode = new String(objectData, modeStart, index - modeStart);
                index++; // skip space

                // Read filename — ends with null byte
                int nameStart = index;
                while (objectData[index] != 0) {
                    index++;
                }
                String name = new String(objectData, nameStart, index - nameStart);
                index++; // skip null byte

                // Skip 20-byte SHA-1 hash
                index += 20;

                entries.add(name);
            }

            // Output entries sorted alphabetically
            Collections.sort(entries);
            for (String entry : entries) {
                System.out.println(entry);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read tree object", e);
        }
    }
}
