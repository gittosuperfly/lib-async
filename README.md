# lib-async
线程池封装库，方便线程池使用

## 引入

[![](https://jitpack.io/v/gittosuperfly/lib-async.svg)](https://jitpack.io/#gittosuperfly/lib-async)


**Step 1**. 添加JitPack repository到你项目的build.gradle文件

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2**. 添加库依赖
```groovy
	dependencies {
	    implementation 'com.github.gittosuperfly:lib-async:Version'
	}
```


## 使用

**Step 1**. 在Application中初始化Async：

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Async.init(this)
    }
}
```

**Step 2**. 代码中使用

```kotlin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
```

## 其他

设置自定义日志发送者：
```kotlin
//设置日志委托
companion object {
    private const val TAG = "ASYNC_LOG"
}

...

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

```
