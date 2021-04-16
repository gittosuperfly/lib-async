package cai.lib.async_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cai.lib.async.Async
import cai.lib.async.AsyncExecutorFactory
import cai.lib.async.AsyncSchedulers
import cai.lib.async.ILoggerDelegate
import java.util.concurrent.Callable

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CAIYUFEI"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //设置日志委托
        Async.setLoggerDelegate(object : ILoggerDelegate {
            override fun log(msg: String) {
                Log.d(TAG, msg)
            }

            override fun warn(msg: String) {
                Log.w(TAG, msg)
            }

            override fun error(msg: String, throwable: Throwable?) {
                Log.e(TAG, msg, throwable)
            }

        })

        //初始化
        Async.init(this)

        /**
         * 基本使用示例：
         */

        //运行任务
        Async.execute {
            println(123)
        }

        Async.submit {
            println(234)
        }

        val call = Callable { "result" }
        Async.submit(call).subscribe {
            println(it)
        }

        //获取默认线程池
        val threadPool = Async.getRunnerThreadPool()
        val cacheThreadPool = Async.getCacheThreadPool()
        val logThreadPool = Async.getLogThreadPool()

        //创建新线程池
        val test1 = AsyncExecutorFactory.createFixedThreadPoolExecutor("test-thread-pool", 4)
        val test2 = AsyncExecutorFactory.createSingleThreadExecutor("test-thread-pool")

        //定义的一些RxJava::Schedulers
        AsyncSchedulers.MAIN
        AsyncSchedulers.ASYNC
        AsyncSchedulers.NETWORK
        AsyncSchedulers.UPLOAD

    }
}