package cai.lib.async

import android.util.Log

/**
 * 日志委托类
 */
interface ILoggerDelegate {
    fun log(msg: String)
    fun warn(msg: String)
    fun error(msg: String, throwable: Throwable? = null)

    companion object {

        private const val TAG = "lib-async"

        internal val DEFAULT = object : ILoggerDelegate {
            override fun log(msg: String) {
                Log.d(TAG, msg)
            }

            override fun warn(msg: String) {
                Log.w(TAG, msg)
            }

            override fun error(msg: String, throwable: Throwable?) {
                Log.e(TAG, msg, throwable)
            }
        }
    }
}