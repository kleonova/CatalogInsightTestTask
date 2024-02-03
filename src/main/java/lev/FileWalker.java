package lev;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileWalker {
    private static final int TIMEWAIT = 120; // seconds
    private Report report;

    private int maxDepth;
    private int countThreads;

    private ExecutorService executor;

    public FileWalker(int countThreads, int maxDepth) {
        report = new Report();
        this.countThreads = countThreads;
        this.maxDepth = maxDepth;
    }

    public void processCatalog(String path) {
        executor = Executors.newFixedThreadPool(countThreads);

        try {
            walk(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(TIMEWAIT, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void walk(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (root.relativize(dir).getNameCount() > maxDepth) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                executor.submit(() -> {
                    processDirectory(dir);
                });
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                executor.submit(() -> {
                    processFile(file);
                });
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Обработка директории
    private void processDirectory(Path dir) {
        System.out.println("Обработка директории: " + dir);
    }

    // Обработка файла
    private void processFile(Path filePath) {
        String filename = "";
        String absPath = "";
        long bytes = 0;

        try {
            filename = filePath.getFileName().toString();
            absPath = filePath.toString();
            bytes = Files.size(filePath);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        System.out.println("Обработка файла: " + filename + ", abs " + absPath + ", size " + bytes);
        ReportFile reportFile = new ReportFile(filename, absPath, bytes);
        report.addFile(reportFile);
    }

    public Report getReport() {
        return report;
    }
}