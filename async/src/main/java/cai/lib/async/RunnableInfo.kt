package cai.lib.async

internal class RunnableInfo(val source: String, val threadName: String, val findSourceCost: Long) {
    val startTimestamp: Long = System.currentTimeMillis()
}