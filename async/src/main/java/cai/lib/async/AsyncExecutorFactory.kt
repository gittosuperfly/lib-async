package cai.lib.async

import java.util.concurrent.*

/**
 * 线程池创建工厂
 */
object AsyncExecutorFactory {

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE_IN_HIGH = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
    private const val CORE_POOL_SIZE_IN_LOW = 2

    /**
     * 创建单线程线程池
     */
    @JvmStatic
    fun createSingleThreadExecutor(name: String): ThreadPoolExecutor {
        return createFixedThreadPoolExecutor(name, 1)
    }

    /**
     * 创建定长线程池
     */
    @JvmStatic
    fun createFixedThreadPoolExecutor(
        name: String,
        size: Int,
        blockingQueue: BlockingQueue<Runnable>? = null
    ): ThreadPoolExecutor {
        val executor: ThreadPoolExecutor = AsyncThreadPoolExecutor(
            corePoolSize = size,
            maximumPoolSize = size,
            keepAliveTime = 1,
            unit = TimeUnit.MINUTES,
            workQueue = blockingQueue ?: LinkedBlockingQueue(),
            threadFactory = AsyncThreadFactory(name)
        )
        executor.allowCoreThreadTimeOut(true)
        return executor
    }

    /**
     * 创建可缓存线程池
     */
    @JvmStatic
    fun createCacheExecutor(name: String): ThreadPoolExecutor {
        return AsyncThreadPoolExecutor(
            corePoolSize = 0,
            maximumPoolSize = Int.MAX_VALUE,
            keepAliveTime = 60L,
            unit = TimeUnit.SECONDS,
            workQueue = SynchronousQueue(),
            threadFactory = AsyncThreadFactory(name)
        )
    }

    /** ========================== 以下方法SDK内部使用 ========================== */

    @JvmStatic
    @JvmName("__01_Async_createRunnerExecutor__")
    internal fun createRunnerExecutor(level: Level): AsyncThreadPoolExecutor =
        (if (level.value < Level.MIDDLE.value) {
            AsyncThreadPoolExecutor(
                corePoolSize = CORE_POOL_SIZE_IN_LOW,
                maximumPoolSize = MAXIMUM_POOL_SIZE,
                keepAliveTime = 2,
                unit = TimeUnit.MINUTES,
                workQueue = LinkedBlockingQueue(512),
                threadFactory = AsyncThreadFactory("async-runner-pool-[low]")
            )
        } else {
            AsyncThreadPoolExecutor(
                corePoolSize = CORE_POOL_SIZE_IN_HIGH,
                maximumPoolSize = MAXIMUM_POOL_SIZE,
                keepAliveTime = 3,
                unit = TimeUnit.MINUTES,
                workQueue = LinkedBlockingQueue(1024),
                threadFactory = AsyncThreadFactory("async-runner-pool-[high]")
            )
        }).apply {
            allowCoreThreadTimeOut(true)
        }

    @JvmStatic
    @JvmName("__02_Async_createCacheExecutor__")
    internal fun createCacheExecutor(): ExecutorService = createCacheExecutor("async_cache_pool")

    //不使用[AsyncThreadPoolExecutor] 是因为Log线程池不需要打运行Log，不然就无线循环打印了...
    @JvmStatic
    @JvmName("__03_Async_createLogExecutor__")
    internal fun createLogExecutor(): ExecutorService = Executors.newSingleThreadExecutor(
        AsyncThreadFactory("async-log-thread")
    )
}