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
            case "ls-tree" -> {
                if (args.length >= 3 && args[1].equals("--name-only")) {
                    String treeSha = args[2];
                    ReadTree.lsTree(treeSha);
                } else {
                    System.out.println("Usage: ls-tree --name-only <tree_sha>");
                }
            }
            case "write-tree" -> WriteTree.execute();
            case "commit-tree" -> {
                String treeSha = args[1];
                String parentSha = null;
                String message = null;

                for (int i = 2; i < args.length; i++) {
                    switch (args[i]) {
                        case "-p" -> parentSha = args[++i];
                        case "-m" -> message = args[++i];
                    }
                }

                if (message == null) {
                    System.err.println("Error: Commit message is required using -m");
                    return;
                }

                String commitSha = CreateCommit.createCommitObject(treeSha, parentSha, message);
                System.out.println(commitSha);
            }
            case "clone" -> {
                if (args.length >= 3) {
                    String repoUrl = args[1];
                    String destinationDir = args[2]; // Pass as String directly
                    try {
                        CloneRepo.cloneRepository(repoUrl, destinationDir); // Pass String to method
                    } catch (Exception e) {  // Catch generic Exception if IOException is not thrown
                        System.err.println("Error cloning repository: " + e.getMessage());
                        e.printStackTrace(); // Optional: for full debug info
                    }
                } else {
                    System.out.println("Usage: clone <repository_url> <destination_dir>");
                }
            }

            default -> System.out.println("Unknown command: " + command);
        }
    }
}
