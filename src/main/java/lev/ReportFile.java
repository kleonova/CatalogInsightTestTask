package lev;

import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Data
public class ReportFile {
    private static final List<String> START_STRINGS = Arrays.asList("#", "rem ", "//");

    private Path path;
    private String name;
    private String absPath;
    private long size; //byte
    private int countAllLines;
    private int countEmptyLines;
    private int countCommentLines;

    public ReportFile(Path filePath) {
        path = filePath;
        name = filePath.getFileName().toString();
        absPath = filePath.toString();
        size = 0;
        try {
            size = Files.size(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        countLines();
    }

    private void countLines() {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                countAllLines++;

                if (line.isEmpty()) {
                    countEmptyLines++;
                } else {
                    for (String startString : START_STRINGS) {
                        if (line.startsWith(startString)) {
                            countCommentLines++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + name + ": " + e.getMessage());
        }
    }
}
