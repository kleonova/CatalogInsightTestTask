package lev;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileWalker {
    private static final int TIMEWAIT = 300; // seconds
    private Report report;

    private int maxDepth;
    private int countThreads;

    private Set<String> includeExt;

    private Set<String> excludeExt;

    private ExecutorService executor;
    private Lock lock;

    public FileWalker(int countThreads, int maxDepth, Set<String> includeExt, Set<String> excludeExt) {
        report = new Report();
        this.countThreads = countThreads;
        this.maxDepth = maxDepth;
        this.includeExt = includeExt;
        this.excludeExt = excludeExt;
    }

    public void processCatalog(String path) {
        executor = Executors.newFixedThreadPool(countThreads);
        lock = new ReentrantLock();

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
                if (root.relativize(dir).getNameCount() > maxDepth && maxDepth !=0) {
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
        String extension = "";
        String filename = "";
        String absPath = "";
        long bytes = 0;

        try {
            filename = filePath.getFileName().toString();
            absPath = filePath.toString();
            bytes = Files.size(filePath);
            extension = filename.substring(filename.lastIndexOf(".") + 1);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        boolean isProcessAsIncludeFile = includeExt.isEmpty() || includeExt.contains(extension);
        boolean isProcessAsExcludeFile = excludeExt.isEmpty() || !excludeExt.contains(extension);
        // проверить что файл попадает под обработку gitignore

        if (!isProcessAsIncludeFile || !isProcessAsExcludeFile) {
            System.out.println(filename + " не обрабатывать");
            return;
        }

        System.out.println("Обработка файла: " + filename + ", abs " + absPath + ", size " + bytes);
        ReportFile reportFile = new ReportFile(filename, absPath, bytes);

        lock.lock();
        report.addFile(reportFile);
        lock.unlock();
    }

    public Report getReport() {
        return report;
    }
}