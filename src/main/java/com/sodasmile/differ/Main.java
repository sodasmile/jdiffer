package com.sodasmile.differ;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sodasmile.differ.checksum.Checker;

/**
 * The main class to start running the Differ
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            Logger.info("USAGE: differ [startdirectory] <startdirectory...>");
            return;
        }

        Differ differ = new Differ(Checker.Algorithm.SHA1);
        Map<String,List<FileInfo>> scanMap = differ.runDiffs(args[0]);
        printInfo(scanMap);
        differ.printRuntimeInfo();

    }

    private static void printInfo(Map<String, List<FileInfo>> scanMap) {
        int i = 0;
        SortedSet<DuplicateHolder> duplicates = new TreeSet<>();
        for (List<FileInfo> fileInfos : scanMap.values()) {
            if (fileInfos.size() > 1) {
                duplicates.add(new DuplicateHolder(fileInfos));
                i++;
            }
        }
        for (DuplicateHolder duplicate : duplicates) {
            Logger.info("Duplicates: {}", duplicate.fileInfos);
        }
        Logger.info("{} suspected duplicates found", i);
    }

    private static class DuplicateHolder implements Comparable<DuplicateHolder>{

        private List<FileInfo> fileInfos;

        public DuplicateHolder(List<FileInfo> fileInfos) {
            this.fileInfos = fileInfos;
        }

        @Override
        public int compareTo(DuplicateHolder other) {
            FileInfo fileInfo = fileInfos.get(0);
            FileInfo otherFileInfo = other.fileInfos.get(0);

            int tsc = fileInfo.getTotalSubCount() - otherFileInfo.getTotalSubCount();
            if (tsc != 0) {
                return tsc;
            }
            int duplicates = fileInfos.size() - other.fileInfos.size();
            if (duplicates != 0) {
                return duplicates;
            }

            return fileInfo.getFile().compareTo(otherFileInfo.getFile());
        }
    }
}
