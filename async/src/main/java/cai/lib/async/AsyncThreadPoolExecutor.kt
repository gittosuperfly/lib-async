package cai.lib.async

import androidx.annotation.WorkerThread
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.*

/**
 * 封装了任务信息输出的线程池
 */
class AsyncThreadPoolExecutor : ThreadPoolExecutor {
    constructor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit?,
        workQueue: BlockingQueue<Runnable>?
    ) : super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue)

    constructor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit?,
        workQueue: BlockingQueue<Runnable>?,
        threadFactory: ThreadFactory?
    ) : super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory)

    constructor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit?,
        workQueue: BlockingQueue<Runnable>?,
        handler: RejectedExecutionHandler?
    ) : super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler)

    constructor(
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit?,
        workQueue: BlockingQueue<Runnable>?,
        threadFactory: ThreadFactory?,
        handler: RejectedExecutionHandler?
    ) : super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler)


    private val threadLocal: ThreadLocal<RunnableInfo> = ThreadLocal()

    @WorkerThread
    fun getSource(parent: Any): String {
        val source = StringBuilder()
        val nameCandidates: MutableList<String> = ArrayList()
        val visited = HashSet<Int>()
        val queue: Queue<Any> = LinkedBlockingQueue()
        queue.offer(parent)
        while (!queue.isEmpty()) {
            val head = queue.poll()!!
            visited.add(head.hashCode())
            val classString = head.javaClass.name
            if (!classString.startsWith("java.util") && !classString.startsWith("io.reactivex")) {
                nameCandidates.add(classString)
            }
            for (f in getPotentialWrapperFields(head.javaClass, head)) {
                if (Modifier.isStatic(f.modifiers)) {
                    continue
                }
                f.isAccessible = true
                try {
                    val obj = f[head]
                    if (obj != null && !visited.contains(obj.hashCode())) {
                        visited.add(obj.hashCode())
                        queue.offer(obj)
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
        return if (nameCandidates.isEmpty()) {
            "Unknown"
        } else {
            for (i in nameCandidates.indices.reversed()) {
                source.append(nameCandidates[i]).append(' ')
                if (source.length >= 100) {
                    break
                }
            }
            source.toString()
        }
    }

    @WorkerThread
    private fun getPotentialWrapperFields(type: Class<*>, o: Any): List<Field> {
        val fields: MutableList<Field> = ArrayList()
        var clazz: Class<*>? = type
        while (clazz != null) {
            for (field in clazz.declaredFields) {
                try {
                    field.isAccessible = true
                    val obj = field[o]
                    if (obj is Runnable || obj is Callable<*> || obj is Observer || obj is Observable || obj is Consumer<*> || obj is Action) {
                        fields.add(field)
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
            clazz = clazz.superclass
        }
        return fields
    }

    override fun beforeExecute(thread: Thread, runnable: Runnable) {
        val startTime = System.currentTimeMillis()
        val source = getSource(runnable)
        threadLocal.set(RunnableInfo(source, thread.name, System.currentTimeMillis() - startTime))
        super.beforeExecute(thread, runnable)
    }

    override fun afterExecute(runnable: Runnable?, throwable: Throwable?) {
        super.afterExecute(runnable, throwable)
        val info = threadLocal.get() ?: return
        val duration: Long = System.currentTimeMillis() - info.startTimestamp
        Async.logger.log(
            toJsonString(info.source, info.threadName, info.findSourceCost, duration)
        )
    }


    private fun toJsonString(source: String, thread: String, cost: Long, duration: Long): String {
        return "{source:\"$source\", threadName:\"$thread\", findSourceCost:$cost, duration: $duration}"
    }

}