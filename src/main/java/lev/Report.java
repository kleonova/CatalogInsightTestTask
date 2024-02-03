package lev;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private int countFiles;
    private long sizeFiles; //byte
    private List<ReportFile> fileList;

    public Report() {
        fileList = new ArrayList<>();
    }

    public int getCountFiles() {
        return countFiles;
    }

    public void setCountFiles(int countFiles) {
        this.countFiles = countFiles;
    }

    public long getSizeFiles() {
        return sizeFiles;
    }

    public void setSizeFiles(long sizeFiles) {
        this.sizeFiles = sizeFiles;
    }

    public List<ReportFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<ReportFile> fileList) {
        this.fileList = fileList;
    }

    public void addFile(ReportFile reportFile) {
        countFiles++;
        sizeFiles += reportFile.getSize();
        fileList.add(reportFile);
    }

    public void print() {
        System.out.println("Информация о каталоге: ");
        System.out.println("countFiles=" + countFiles);
        System.out.println("sizeFiles=" + (sizeFiles / 1024));
    }
}
