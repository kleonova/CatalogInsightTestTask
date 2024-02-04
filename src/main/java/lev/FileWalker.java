package lev;

import lombok.Builder;
import lombok.Data;

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

@Data
@Builder
public class FileWalker {
    private static final int TIMEWAIT = 300; // seconds
    private Report report;

    private int maxDepth;
    private int countThreads;
    private Set<String> includeExt;
    private Set<String> excludeExt;
    private Set<String> rulesByNameGitIgnore;
    private Set<String> rulesByExtGitIgnore;

    private ExecutorService executor;
    private Lock lock;

    public void processCatalog(String path) throws IOException {
        report = new Report();
        executor = Executors.newFixedThreadPool(countThreads);
        lock = new ReentrantLock();

        walk(Path.of(path));
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

                if (!rulesByNameGitIgnore.isEmpty() && rulesByNameGitIgnore.contains(root.relativize(dir).toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }

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

    // Обработка файла
    private void processFile(Path filePath) {
        String filename = filePath.getFileName().toString();

        if (canProcessFile(filename)) {
            ReportFile reportFile = new ReportFile(filePath);

            lock.lock();
            report.addFile(reportFile);
            lock.unlock();
        }
    }

    private boolean canProcessFile(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);

        boolean isProcessAsIncludeFile = includeExt.isEmpty() || includeExt.contains(extension);
        boolean isProcessAsExcludeFile = excludeExt.isEmpty() || !excludeExt.contains(extension);
        boolean isProcessAsExtGitIgnore = rulesByExtGitIgnore.isEmpty() || !rulesByExtGitIgnore.contains(extension);
        boolean isProcessAsNameGitIgnore = rulesByNameGitIgnore.isEmpty() || !rulesByNameGitIgnore.contains(filename);

        return isProcessAsIncludeFile && isProcessAsExcludeFile && isProcessAsExtGitIgnore && isProcessAsNameGitIgnore;
    }
}