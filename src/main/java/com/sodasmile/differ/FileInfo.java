package com.sodasmile.differ;

import java.io.File;

/**
* Holds information about scanned/checked file.
*/
public class FileInfo {

    private String checkSum;
    private File file;
    private int fileCount;
    private int totalSubCount;

    FileInfo(File file, String checkSum) {
        this(file, checkSum, 1, 0);
    }

    FileInfo(File file, String checkSum, int fileCount, int totalSubCount) {
        this.file = file;
        this.checkSum = checkSum;
        this.fileCount = fileCount;
        this.totalSubCount = totalSubCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        return checkSum.equals(fileInfo.checkSum);
    }

    @Override
    public int hashCode() {
        return checkSum.hashCode();
    }

    @Override
    public String toString() {
        return file + "[tsc:" + totalSubCount + "]";
    }

    public String getCheckSum() {
        return checkSum;
    }

    public File getFile() {
        return file;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getTotalSubCount() {
        return totalSubCount;
    }
}
