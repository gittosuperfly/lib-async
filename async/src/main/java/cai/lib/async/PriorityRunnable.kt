package cai.lib.async

import android.os.Process

/**
 * 可修改优先级的Runnable
 */
class PriorityRunnable internal constructor(
    private val runnable: Runnable,
    private val priority: Int
) : Runnable {
    override fun run() {
        Process.setThreadPriority(priority)
        runnable.run()
    }
}