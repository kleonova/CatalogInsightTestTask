package lev;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    private static String directoryPath;
    private static boolean isRecursive = false;
    private static int maxDepth = 0;
    private static int countThreads = 1;
    private List<String> includeExt = new ArrayList<>();
    private List<String> excludeExt;
    private static boolean useGitIgnore = false;
    private List<String> formatReport;

    public static void main( String[] args ) throws Exception {
        initArgs(args);
        printArgs();

        FileWalker fileWalker = new FileWalker(countThreads, maxDepth);
        fileWalker.processCatalog(directoryPath);
        fileWalker.getReport().print();
    }

    private static void initArgs(String[] args) throws Exception {
        directoryPath = args[0];
        if (!Files.exists(Paths.get(directoryPath))) {
            throw new Exception("Директория не существует");
        }

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--recursive")) {
                isRecursive = true;
            }

            if (args[i].equals("--gitignore")) {
                useGitIgnore = true;
            }

            if (args[i].contains("--max-depth")) {
                maxDepth = Integer.parseInt(args[i].split("=")[1]);
            }

            if (args[i].contains("--thread")) {
                countThreads = Integer.parseInt(args[i].split("=")[1]);
            }
        }

        if (!isRecursive) {
            maxDepth = 1;
        }
    }

    private static void printArgs() {
        System.out.println("Параметры запуска:");
        System.out.println("Путь " + directoryPath);
        System.out.println("Запуск рекурсивно " + isRecursive);
        System.out.println("Глубина обхода " + (maxDepth == 0 ? "не задана" : maxDepth));
        System.out.println("Количество потоков для обхода " + countThreads);
        System.out.println("Обрабатывать файлы с расширением " );
        System.out.println("Не обрабатывать файлы с расширением ");
        System.out.println("Не обрабатывать файлы, указанные в gitignore " + useGitIgnore);
        System.out.println("Формат вывода ");
    }
}
