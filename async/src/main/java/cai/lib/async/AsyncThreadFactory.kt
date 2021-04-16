package cai.lib.async

import android.os.Process
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 线程创建工厂
 */
class AsyncThreadFactory(threadPoolName: String) : ThreadFactory {

    companion object {
        private val poolNumber = AtomicInteger(1)
    }

    private val threadNumber = AtomicInteger(1)
    private val group: ThreadGroup
    private val namePrefix: String

    init {
        val security = System.getSecurityManager()
        group = if (security != null) security.threadGroup else Thread.currentThread().threadGroup!!
        namePrefix = threadPoolName + '-' + poolNumber.getAndIncrement() + '-'
    }

    override fun newThread(runnable: Runnable?): Thread {
        val run = runnable ?: Runnable {}
        val priorityRunnable = PriorityRunnable(run, Process.THREAD_PRIORITY_BACKGROUND)
        val thread = Thread(group, priorityRunnable, namePrefix + threadNumber.getAndIncrement(), 0)
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }
        return thread
    }
}