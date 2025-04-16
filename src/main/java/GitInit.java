import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class GitInit {
    public static void run() {
        File gitDir = new File(".git");
        File objectsDir = new File(gitDir, "objects");
        File refsDir = new File(gitDir, "refs");
        File headFile = new File(gitDir, "HEAD");
        File configFile = new File(gitDir, "config");

        try {
            // Create .git directory and subdirectories
            if (!gitDir.exists() && !gitDir.mkdirs()) {
                throw new IOException("Failed to create .git directory");
            }

            if (!objectsDir.exists() && !objectsDir.mkdirs()) {
                throw new IOException("Failed to create objects directory");
            }

            if (!refsDir.exists() && !refsDir.mkdirs()) {
                throw new IOException("Failed to create refs directory");
            }

            // Write HEAD file
            if (!headFile.exists()) {
                headFile.createNewFile();
                Files.write(headFile.toPath(), "ref: refs/heads/main\n".getBytes());
            }

            // Write config file
            if (!configFile.exists()) {
                configFile.createNewFile();
                String configContent = "[core]\n" +
                                       "\trepositoryformatversion = 0\n" +
                                       "\tfilemode = true\n" +
                                       "\tbare = false\n";
                Files.write(configFile.toPath(), configContent.getBytes());
            }

            System.out.println("Initialized empty Git repository in " + gitDir.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to initialize Git repository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

