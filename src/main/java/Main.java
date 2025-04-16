import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No command provided.");
            return;
        }

        String command = args[0];

        switch (command) {
            case "init" -> GitInit.run();
            case "cat-file" -> {
                if (args.length >= 3 && args[1].equals("-p")) {
                    String hash = args[2];
                    ReadBlob.printBlob(hash);
                } else {
                    System.out.println("Usage: cat-file -p <hash>");
                }
            }
            case "hash-object" -> {
                if (args.length >= 3 && args[1].equals("-w")) {
                    String filePath = args[2];
                    CreateBlob.hashAndWrite(filePath);
                } else {
                    System.out.println("Usage: hash-object -w <file>");
                }
            }
          

            default -> System.out.println("Unknown command: " + command);
        }
    }
}
