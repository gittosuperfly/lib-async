package cai.lib.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;

/**
 * 线程创建工厂
 */
public class AsyncThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    public AsyncThreadFactory(String threadPoolName) {
        SecurityManager security = System.getSecurityManager();
        group = (security != null) ? security.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = threadPoolName + '-' + poolNumber.getAndIncrement() + '-';
    }


    @Override
    public Thread newThread(Runnable runnable) {
        runnable = runnable != null ? runnable : () -> { /* ignore */ };
        PriorityRunnable pr = new PriorityRunnable(runnable, Process.THREAD_PRIORITY_BACKGROUND);
        Thread thread = new Thread(group, pr, namePrefix + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
