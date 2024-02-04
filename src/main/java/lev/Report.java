package lev;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Report {
    private int countFiles;
    private long sizeFiles; //byte
    private int countAllLines;
    private int countEmptyLines;
    private int countCommentLines;

    public void addFile(ReportFile reportFile) {
        countFiles++;
        sizeFiles += reportFile.getSize();
        countAllLines += reportFile.getCountAllLines();
        countEmptyLines += reportFile.getCountEmptyLines();
        countCommentLines += reportFile.getCountCommentLines();
    }

    public void print() {
        System.out.println("\n=== Информация о каталоге ===");
        System.out.println("Всего файлов: " + countFiles);
        System.out.println("Общий размер файлов: " + (sizeFiles / 1024) + "КБ");
        System.out.println("Всего строк: " + countAllLines);
        System.out.println("Всего пустых строк: " + countEmptyLines);
        System.out.println("Всего однострочных комментариев: " + countCommentLines);
        System.out.println();
    }

    @Override
    public String toString() {
        return String.format("""
                        countFiles=%d
                        sizeFiles=%d
                        countAllLines=%d
                        countEmptyLines=%d
                        countCommentLines=%d
                        """,
                countFiles, sizeFiles, countAllLines, countEmptyLines, countCommentLines);
    }
}
