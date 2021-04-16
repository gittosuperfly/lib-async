package cai.lib.async;

import android.os.Process;

/**
 * 可修改优先级的Runnable
 */
public class PriorityRunnable implements Runnable {

    final int priority;
    final Runnable runnable;

    PriorityRunnable(Runnable runnable, int priority) {
        this.runnable = runnable;
        this.priority = priority;
    }

    @Override
    public void run() {
        Process.setThreadPriority(priority);
        runnable.run();
    }
}