package lev;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class App {
    private static String directoryPath;
    private static boolean isRecursive = false;
    private static int maxDepth = 0;
    private static int countThreads = 1;
    private static Set<String> includeExt = Collections.emptySet();
    private static Set<String> excludeExt = Collections.emptySet();
    private static boolean useGitIgnore = false;
    private static Set<String> formatReport = Collections.emptySet();
    private static Set<String> rulesByNameGitIgnore = new HashSet<>();
    private static Set<String> rulesByExtGitIgnore = new HashSet<>();

    public static void main( String[] args ) throws Exception {
        initArgs(args);
        printArgs();

        if (useGitIgnore) {
            readGitIgnoreRules();
        }

        FileWalker fileWalker = FileWalker
                .builder()
                .countThreads(countThreads)
                .maxDepth(maxDepth)
                .includeExt(includeExt)
                .excludeExt(excludeExt)
                .rulesByNameGitIgnore(rulesByNameGitIgnore)
                .rulesByExtGitIgnore(rulesByExtGitIgnore)
                .build();
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
                maxDepth = getNumericArg(args[i]);
            }

            if (args[i].contains("--thread")) {
                countThreads = getNumericArg(args[i]);
            }

            if (args[i].contains("--include-ext")) {
                includeExt = getSelectionArg(args[i]);
            }

            if (args[i].contains("--exclude-ext")) {
                excludeExt = getSelectionArg(args[i]);
            }

            if (args[i].contains("--output")) {
                formatReport = getSelectionArg(args[i]);
            }
        }

        // если не задан флаг рекурсивно - отрабатывать только файлы в корне
        if (!isRecursive) {
            maxDepth = 1;
        }

        // если конфликт includeExt и excludeExt ?
    }

    private static int getNumericArg(String argument) {
        String argValues = argument.split("=")[1];
        if (argValues.isEmpty() ) {
            return 0;
        }

        return Integer.parseInt(argValues);
    }

    private static Set<String> getSelectionArg(String argument) {
        String argValues = argument.split("=")[1];
        if (argValues.isEmpty()) {
            return Collections.emptySet();
        }

        return new HashSet<>(Arrays.asList(argValues.split(",")));
    }

    private static void printArgs() {
        String useIncludeExt = includeExt.isEmpty() ? "не заданo" : includeExt.toString();
        String useExcludeExt = excludeExt.isEmpty() ? "не заданo" : excludeExt.toString();
        String useFormatReport = formatReport.isEmpty() ? "не заданo" : formatReport.toString();

        System.out.println("Параметры запуска:");
        System.out.println("Путь: " + directoryPath);
        System.out.println("Запуск рекурсивно: " + isRecursive);
        System.out.println("Глубина обхода: " + (maxDepth == 0 ? "не задана" : maxDepth));
        System.out.println("Количество потоков для обхода: " + countThreads);
        System.out.println("Oбрабатывать файлы с расширением: " + useIncludeExt);
        System.out.println("Не обрабатывать файлы с расширением: " + useExcludeExt);
        System.out.println("Не обрабатывать файлы, указанные в gitignore: " + useGitIgnore);
        System.out.println("Формат вывода: " + useFormatReport);
    }

    private static void readGitIgnoreRules() {
        Path gitignorePath = Paths.get(directoryPath + "/.gitignore");

        try (BufferedReader reader = Files.newBufferedReader(gitignorePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("*.")) {
                    rulesByExtGitIgnore.add(line.replaceAll("\\*\\.", ""));
                } else {
                    rulesByNameGitIgnore.add(line.replaceAll("/", ""));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла .gitignore: " + e.getMessage());
        }
    }
}
