package io.appcheck.synchronizer.utils

import android.util.Log

object Logger {

    private val isLogsEnabled = true

    private fun getClassName(`object`: Any): String {
        var className = `object`.javaClass.name
        var firstPosition = className.lastIndexOf(".") + 1
        if (firstPosition < 0) {
            firstPosition = 0
        }
        className = className.substring(firstPosition)
        firstPosition = className.lastIndexOf("$")
        if (firstPosition > 0) {
            className = className.substring(0, firstPosition)
        }
        return className
    }

    @JvmStatic
    fun e(`object`: Any, message: String) {
        if (isLogsEnabled)
            Log.e(
                `object` as? String ?: getClassName(`object`),
                message
            )
    }

    @JvmStatic
    fun e(`object`: Any, message: String?, exception: Throwable?) {
        if (isLogsEnabled)
            Log.e(
                `object` as? String ?: getClassName(`object`),
                message,
                exception
            )
    }

    @JvmStatic
    fun i(`object`: Any, message: String) {
        if (isLogsEnabled)
            Log.i(
                `object` as? String ?: getClassName(`object`),
                message
            )
    }

    @JvmStatic
    fun d(`object`: Any, message: String) {
        if (isLogsEnabled)
            Log.d(
                `object` as? String ?: getClassName(`object`),
                message
            )
    }

    @JvmStatic
    fun v(`object`: Any, message: String) {
        if (isLogsEnabled)
            Log.v(
                `object` as? String ?: getClassName(`object`),
                message
            )
    }

    @JvmStatic
    fun w(`object`: Any, message: String) {
        if (isLogsEnabled)
            Log.w(
                `object` as? String ?: getClassName(`object`),
                message
            )
    }

    @JvmStatic
    fun w(`object`: Any, message: String, exception: Exception) {
        if (isLogsEnabled)
            Log.w(
                `object` as? String ?: getClassName(`object`),
                message,
                exception
            )
    }
}
