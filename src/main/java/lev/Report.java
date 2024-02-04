package lev;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Report {
    private int countFiles;
    private long sizeFiles; //byte
    private int countAllLines;
    private int countEmptyLines;
    private int countCommentLines;
    private List<ReportFile> fileList;

    public Report() {
        fileList = new ArrayList<>();
    }

    public void addFile(ReportFile reportFile) {
        countFiles++;
        sizeFiles += reportFile.getSize();
        countAllLines += reportFile.getCountAllLines();
        countEmptyLines += reportFile.getCountEmptyLines();
        countCommentLines += reportFile.getCountCommentLines();

        fileList.add(reportFile);
    }

    public void print() {
        System.out.println("Информация о каталоге: ");
        System.out.println("Всего файлов: " + countFiles);
        System.out.println("Общий размер файлов: " + (sizeFiles / 1024) + "КБ");
        System.out.println("Всего строк: " + countAllLines);
        System.out.println("Всего пустых строк: " + countEmptyLines);
        System.out.println("Всего однострочных комментариев: " + countCommentLines);
    }
}
