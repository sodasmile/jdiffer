package com.sodasmile.differ;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sodasmile.differ.checksum.Checker;

public class Differ {

    private final Checker.Algorithm algorithm;

    private Map<String, List<FileInfo>> scanMap = new HashMap<>();
    private long scanCount = 0;
    private long start;
    private long stop;
    private final String msgTemplate = "Scanned {} files, {} suspected unique, {} suspected duplicates. Runtime {}s";

    public Differ(Checker.Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    private class MonitorThread implements Runnable {

        private boolean running = true;

        private final boolean idea = detectIntelliJ();
        private final String postFix;
        private static final String backspaces = "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b"
                + "\b\b\b\b\b\b\b\b\b\b";

        {
            if (idea) {
                postFix = "%n";
            } else {
                postFix = "";
            }
        }

        @Override
        public void run() {
            while (running) {
                printInfo();

                Thread.yield();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.debug("Interupted");
                }
            }
        }

        private void printInfo() {
            if (!idea) {
                System.out.print(backspaces);
            }

            printGeneralInfo(postFix);
        }
    }

    private void printGeneralInfo(String postFix) {
        long count = scanCount;
        int size = scanMap.size();

        long totalBytesRead = Checker.getTotalBytesRead();
        long totalRuntimeMs = Checker.getTotalDigestTime();
        if (totalRuntimeMs == 0) {
            totalRuntimeMs = 1;
        }
        double runtimeSec = totalRuntimeMs/1000D;
        double idleTimeSec = Checker.getTotalIdleTime()/1000D;
        double bytesPrMs = totalBytesRead / totalRuntimeMs;
        double kbpms = bytesPrMs / 1024D;
        double mbpms = kbpms / 1024D;
        double mbps = mbpms * 1000D;

        Logger.debugnln(String.format(
                "Scanned %d files, %d suspected unique, %d suspected duplicates. Runtime %.2fs Total bytes read %,dMB, "
                        + "total digesttime: %.2fs, digest idle time: %.2fs, %.0fB/ms, %.2fMB/s%s%n",
                count,
                size,
                count - size,
                (System.currentTimeMillis() - start) / 1000D,
                totalBytesRead / (1024 * 1024),
                runtimeSec,
                idleTimeSec,
                bytesPrMs,
                mbps,
                postFix));
    }

    public Map<String, List<FileInfo>> runDiffs(String... startDirectories) {

        MonitorThread monitorThread = new MonitorThread();
        start = System.currentTimeMillis();
        new Thread(monitorThread).start();
        for (String directory : startDirectories) {
            File file = new File(directory);
            runDiffs(file);
        }

        monitorThread.running = false;
        stop = System.currentTimeMillis();
        Logger.info("%nScanning done.");
        return scanMap;
    }

    public void printRuntimeInfo() {
        printGeneralInfo("%n");
    }

    private static boolean detectIntelliJ() {
        boolean idea = false;
        Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (((String) entry.getKey()).contains("idea.") || ((String) entry.getValue()).contains("idea."))
                idea = true;
        }
        return idea;
    }

    private FileInfo runDiffs(File file) {
        FileInfo checkSum;
        if (file.isDirectory()) {
            checkSum = scanDir(file);
        } else {
            checkSum = scanFile(file);
        }
        mput(scanMap, checkSum.getCheckSum(), checkSum);

        return checkSum;
    }

    static <K, V> void mput(Map<K, List<V>> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<V>());
        }
        map.get(key).add(value);
    }

    private FileInfo scanFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String checkSum = Checker.getCheckSum(fileInputStream, algorithm);
            scanCount++;
            return new FileInfo(file, checkSum);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private FileInfo scanDir(File file) {
        File[] files = file.listFiles();
        String checkSums = "";
        int totalSubCount = 0;
        if (files != null && files.length > 0) {
            for (File file1 : files) {
                FileInfo subFileInfo = runDiffs(file1);
                totalSubCount+=subFileInfo.getFileCount() + subFileInfo.getTotalSubCount();
                checkSums += subFileInfo.getCheckSum();
            }
        } else {
            checkSums += file;
        }

        scanCount++;
        String checkSum = Checker.getCheckSum(checkSums, algorithm);
        return new FileInfo(file, checkSum, files != null ? files.length : 0, totalSubCount);
    }
}
