package cai.lib.async

import android.app.ActivityManager
import android.content.Context

/**
 * 设备性能等级划分
 *
 * 根据CUP核心数与内存大小设置
 */
internal enum class Level(var value: Int) {

    BEST(5), HIGH(4), MIDDLE(3), LOW(2), BAD(1), UN_KNOW(-1);

    companion object {

        private var totalMemory: Long = 0
        private var lowMemoryThreshold: Long = 0
        private var memoryClass = 0
        private var MB = (1024 * 1024).toLong()


        private var deviceLevel: Level? = null

        @JvmStatic
        fun getLevel(context: Context): Level {
            if (deviceLevel != null) {
                return deviceLevel as Level
            }
            getTotalMemory(context)
            val coresNum = Runtime.getRuntime().availableProcessors()
            if (totalMemory >= 4 * 1024 * MB) {
                deviceLevel = BEST
            } else if (totalMemory >= 3 * 1024 * MB) {
                deviceLevel = HIGH
            } else if (totalMemory >= 2 * 1024 * MB) {
                when {
                    coresNum >= 4 -> {
                        deviceLevel = HIGH
                    }
                    coresNum >= 2 -> {
                        deviceLevel = MIDDLE
                    }
                    coresNum > 0 -> {
                        deviceLevel = LOW
                    }
                }
            } else if (totalMemory >= 1024 * MB) {
                when {
                    coresNum >= 4 -> {
                        deviceLevel = MIDDLE
                    }
                    coresNum >= 2 -> {
                        deviceLevel = LOW
                    }
                    coresNum > 0 -> {
                        deviceLevel = LOW
                    }
                }
            } else if (0 <= totalMemory && totalMemory < 1024 * MB) {
                deviceLevel = BAD
            } else {
                deviceLevel = UN_KNOW
            }
            return deviceLevel as Level
        }

        @JvmStatic
        private fun getTotalMemory(context: Context): Long {
            if (0L != totalMemory) {
                return totalMemory
            }
            val memoryInfo = ActivityManager.MemoryInfo()
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.getMemoryInfo(memoryInfo)
            totalMemory = memoryInfo.totalMem
            lowMemoryThreshold = memoryInfo.threshold

            val maxMemory = Runtime.getRuntime().maxMemory()
            memoryClass = if (maxMemory == Long.MAX_VALUE) {
                am.memoryClass
            } else {
                (maxMemory / MB).toInt()
            }
            return totalMemory
        }

    }
}