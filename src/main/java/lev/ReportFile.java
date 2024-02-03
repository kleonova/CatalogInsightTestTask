package lev;

public class ReportFile {
    private String name;
    private String absPath;
    private long size; //byte
    private int countLines;
    private int countEmptyLines;
    private int countCommentLines;

    public ReportFile(String name, String absPath) {
        this.name = name;
        this.absPath = absPath;
    }

    public ReportFile(String name, String absPath, long sizeBytes) {
        this.name = name;
        this.absPath = absPath;
        this.size = sizeBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCountLines() {
        return countLines;
    }

    public void setCountLines(int countLines) {
        this.countLines = countLines;
    }

    public int getCountEmptyLines() {
        return countEmptyLines;
    }

    public void setCountEmptyLines(int countEmptyLines) {
        this.countEmptyLines = countEmptyLines;
    }

    public int getCountCommentLines() {
        return countCommentLines;
    }

    public void setCountCommentLines(int countCommentLines) {
        this.countCommentLines = countCommentLines;
    }

    @Override
    public String toString() {
        return "ReportFile{" +
                "name='" + name + '\'' +
                ", absPath='" + absPath + '\'' +
                ", size=" + size +
                ", countLines=" + countLines +
                ", countEmptyLines=" + countEmptyLines +
                ", countCommentLines=" + countCommentLines +
                '}';
    }
}
