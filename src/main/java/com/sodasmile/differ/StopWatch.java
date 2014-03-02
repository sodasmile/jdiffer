package com.sodasmile.differ;

public class StopWatch {

    private long start;
    private long elapsed;
    private boolean isRunning;

    /**
     * Starts stopwatch. If running does nothing.
     * @return {@code this} for chaining
     */
    public StopWatch start() {
        if (!isRunning) {
            isRunning = true;
            start = System.currentTimeMillis();
        }
        return this;
    }

    /**
     * Stops the stopwatch. If not running, does nothing.
     * @return {@code this} for chaining
     */
    public StopWatch stop() {
        if (isRunning) {
            isRunning = false;
            long duration = System.currentTimeMillis() - start;
            elapsed += duration;
        }
        return this;
    }

    /**
     * @return total elapsed time, or intermediate time if is not stopped.
     */
    public long getElapsed() {
        if (!isRunning) {
            return elapsed;
        }

        long duration = System.currentTimeMillis() - start;
        return elapsed + duration;
    }
}
