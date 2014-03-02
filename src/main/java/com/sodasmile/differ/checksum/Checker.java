package com.sodasmile.differ.checksum;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sodasmile.differ.StopWatch;

/**
 * Calculates a checksum somehow.
 */
public class Checker {

    public enum Algorithm {
        SHA1("SHA1");
        private String algorithm;

        Algorithm(String algorithm) {
            this.algorithm = algorithm;
        }
    }

    private static long totalBytesRead = 0;
    private static StopWatch digestWatch = new StopWatch();
    private static StopWatch idleWatch = new StopWatch().start();

    public static String getCheckSum(String string, Algorithm algorithm) {
        return getCheckSum(new ByteArrayInputStream(string.getBytes()), algorithm);
    }

    public static String getCheckSum(InputStream inputStream, Algorithm algorithm) {
        idleWatch.stop();
        digestWatch.start();
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm.algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new CheckSumException(e);
        }
        byte[] dataBytes = new byte[1024];

        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(dataBytes)) != -1) {
                messageDigest.update(dataBytes, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
        } catch (IOException e) {
            throw new CheckSumException(e);
        }

        byte[] mdBytes = messageDigest.digest();

        //convert the byte to hex format
        StringBuilder sb = new StringBuilder();
        for (byte mdbyte : mdBytes) {
            sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
        }
        long stop = System.currentTimeMillis();
        digestWatch.stop();
        idleWatch.start();
        return sb.toString();
    }

    public static long getTotalBytesRead() {
        return totalBytesRead;
    }

    public static long getTotalDigestTime() {
        return digestWatch.getElapsed();
    }

    public static long getTotalIdleTime() {
        return idleWatch.getElapsed();
    }

    public static class CheckSumException extends RuntimeException {

        public CheckSumException(Throwable e) {
            super(e);
        }
    }
}
