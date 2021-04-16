package cai.lib.async

import cai.lib.async.Async.logExecutor


/**
 * 日志打印者
 */
internal class Logger {

    private var logDelegate: ILoggerDelegate = ILoggerDelegate.DEFAULT

    fun update(logger: ILoggerDelegate) {
        this.logDelegate = logger
    }

    fun log(msg: String) {
        logExecutor.execute {
            logDelegate.log(msg)
        }
    }

    fun warn(msg: String) {
        logExecutor.execute {
            logDelegate.warn(msg)
        }
    }

    fun error(msg: String, throwable: Throwable?) {
        logExecutor.execute {
            logDelegate.error(msg)
        }
    }
}