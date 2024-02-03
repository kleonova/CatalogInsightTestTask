package lev;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileWalker {
    private ExecutorService executorService;
    private int maxDepth;

    public FileWalker(int countThreads, int maxDepth) {
        this.maxDepth = maxDepth;
        this.executorService = Executors.newFixedThreadPool(countThreads);
    }

    public void walkMultiThread(File root) throws Exception {
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    Future<?> future = executorService.submit(() -> {
                        try {
                            walkMultiThread(file);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    future.get();
                }
            }
        } else {
            handleFile(root);
        }
    }

    public void getFileTree(File file, int depth) {
        // файл
        if (!file.isDirectory()) {
            handleFile(file);
            return;
        }

        // директория
        File[] folderFiles = file.listFiles();
        depth++;
        if (depth > maxDepth && maxDepth!=0) {
            return;
        }

        for (File folderFile : folderFiles ) {
            // несколько потоков
            getFileTree(folderFile, depth);
        }
    }

    public void walk(File root) {
        if (root.isDirectory()) {
            System.out.println(root.getName());
            File[] files = root.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Рекурсивно обходим подкаталог
                        walk(file);
                    } else {
                        // Действие, которое нужно выполнить для каждого файла
                        handleFile(file);
                    }
                }
            }
        }
    }

    private void handleFile(File file) {
        // Обработка файла
        System.out.println("Обработка файла: " + file.getAbsolutePath());
    }
}