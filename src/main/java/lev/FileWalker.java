package lev;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileWalker {
    private Report report;
    private int maxDepth;

    private ExecutorService executorService;

    public FileWalker(int countThreads, int maxDepth) {
        this.maxDepth = maxDepth;

        report = new Report();
        executorService = Executors.newFixedThreadPool(countThreads);
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

    private void handleFile(File file) {
        // Обработка файла
        ReportFile reportFile = new ReportFile(file.getName(), file.getAbsolutePath(), file.length());
        report.addFile(reportFile);
        System.out.println("Обработка файла: " + file.getName() + ", size " + reportFile.getSize());
    }

    public Report getReport() {
        return report;
    }
}