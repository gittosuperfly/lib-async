package cai.lib.async

import android.content.Context
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

object Async {

    private lateinit var threadPoolExecutor: ExecutorService
    private lateinit var cacheExecutor: ExecutorService
    internal var logExecutor: ExecutorService = AsyncExecutorFactory.createLogExecutor()

    private lateinit var deviceLevel: Level
    private lateinit var scheduler: Scheduler

    private val isInit = AtomicBoolean(false)

    @JvmField
    internal val logger = Logger()

    @JvmStatic
    @Synchronized
    fun init(context: Context) {
        if (!isInit.compareAndSet(false, true)) {
            logger.warn("Async already initialized.")
            return
        }
        deviceLevel = Level.getLevel(context)
        threadPoolExecutor = AsyncExecutorFactory.createRunnerExecutor(deviceLevel)
        cacheExecutor = AsyncExecutorFactory.createCacheExecutor()
        scheduler = Schedulers.from(threadPoolExecutor)
        logger.log("Async initialization complete!")
    }

    @JvmStatic
    fun setLoggerDelegate(loggerDelegate: ILoggerDelegate) {
        logger.update(loggerDelegate)
    }

    @JvmStatic
    fun execute(runnable: Runnable) {
        checkIsInit()
        threadPoolExecutor.execute(runnable)
    }

    @JvmStatic
    fun submit(runnable: Runnable): Future<*>? {
        checkIsInit()
        return threadPoolExecutor.submit(runnable)
    }

    @JvmStatic
    fun <V> submit(callable: Callable<out V>): Observable<out V> {
        checkIsInit()
        return Observable.fromCallable(callable)
            .subscribeOn(scheduler)
            .observeOn(AndroidSchedulers.mainThread())
    }

    @JvmStatic
    fun getCacheThreadPool(): ExecutorService {
        checkIsInit()
        return cacheExecutor
    }

    @JvmStatic
    fun getRunnerThreadPool(): ExecutorService {
        checkIsInit()
        return threadPoolExecutor
    }

    @JvmStatic
    fun getLogThreadPool(): ExecutorService = logExecutor

    private fun checkIsInit() {
        if (!isInit.get()) {
            throw Exception("Async has not been initialized yet. Please run: Async.init(context: Context)")
        }
    }
}