package cai.lib.async

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * 通用的RxJava3-Scheduler
 */
object AsyncSchedulers {

    /**
     * UI线程
     */
    @JvmField
    val MAIN: Scheduler = AndroidSchedulers.mainThread()

    /**
     * 用于执行网络请求
     */
    @JvmField
    val NETWORK: Scheduler = Schedulers.from(
        AsyncExecutorFactory.createFixedThreadPoolExecutor("retrofit-api-thread", 4)
    )

    /**
     * 用于执行异步任务, 例如读写缓存, 读写pref, 额外的API请求等
     */
    @JvmField
    val ASYNC: Scheduler = Schedulers.from(Async.getCacheThreadPool())

    /**
     * 用于执行上传操作
     */
    @JvmField
    val UPLOAD: Scheduler = Schedulers.from(
        AsyncExecutorFactory.createFixedThreadPoolExecutor("retrofit-upload-thread", 4)
    )
}