import java.io.*;
import java.net.*;
import java.nio.file.*;

public class CloneRepo {
    
    public static void cloneRepository(String repoUrl, String destinationDir) throws IOException {
        // Check if the destination directory exists, if not create it
        File destDir = new File(destinationDir);
        if (!destDir.exists()) {
            boolean created = destDir.mkdirs();
            if (!created) {
                throw new IOException("Failed to create destination directory.");
            }
            System.out.println("[DEBUG] Destination directory created: " + destinationDir);
        } else {
            System.out.println("[DEBUG] Destination directory already exists: " + destinationDir);
        }

        // Convert URL to the GitHub repository's clone URL
        String cloneUrl = repoUrl.endsWith(".git") ? repoUrl : repoUrl + ".git";
        
        // Log the final clone URL
        System.out.println("[DEBUG] Final clone URL: " + cloneUrl);
        
        // The following are steps to simulate the Git clone operation
        try {
            System.out.println("[DEBUG] Starting the clone process...");
            
            // Clone the repository using `git` command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("git", "clone", cloneUrl, destinationDir);
            
            // Set up process IO for better debugging
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            
            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("[DEBUG] Process finished with exit code: " + exitCode);
            
            if (exitCode == 0) {
                System.out.println("[INFO] Repository cloned successfully.");
            } else {
                System.err.println("[ERROR] Failed to clone the repository. Exit code: " + exitCode);
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] IOException occurred during cloning: " + e.getMessage());
            e.printStackTrace(); // Detailed error stack trace for debugging
            throw new IOException("Error cloning repository: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            System.err.println("[ERROR] Process was interrupted: " + e.getMessage());
            e.printStackTrace(); // Detailed error stack trace for debugging
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }
    
    public static void main(String[] args) {
        if (args.length >= 2) {
            String repoUrl = args[0];
            String destinationDir = args[1];
            
            System.out.println("[DEBUG] Arguments received: Repo URL = " + repoUrl + ", Destination Directory = " + destinationDir);
            
            try {
                cloneRepository(repoUrl, destinationDir);
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to clone repository: " + e.getMessage());
            }
        } else {
            System.out.println("[ERROR] Usage: clone <repository_url> <destination_directory>");
        }
    }
}
