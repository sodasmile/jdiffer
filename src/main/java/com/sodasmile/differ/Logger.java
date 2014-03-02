package com.sodasmile.differ;

/**
 * Poor mans logging impl
 */
public final class Logger {

    private Logger() {
    }

    public static void info(String msg, Object... args) {
        output("INFO: ", msg, "%n", args);
    }

    public static void debug(String msg, Object... args) {
        output("DEBUG: ", msg, "%n", args);
    }

    public static void debugnln(String msg, Object... args) {
        output("DEBUG: ", msg, "", args);
    }

    private static void output(String prefix, String msg, String postFix, Object... args) {
        System.out.printf(prefix + msg.replaceAll("\\{\\}", "%s") + postFix, args);
    }
}
