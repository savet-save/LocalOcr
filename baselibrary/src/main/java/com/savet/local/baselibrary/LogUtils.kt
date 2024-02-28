package com.savet.local.baselibrary

import android.util.Log

/**
 * 用于统一日志输出
 * @author savet
 * @date  2024/2/27
 */
object LogUtils {

    private const val TAG = "LogUtils"

    private val DEBUG_MODE: Int =
        if (BuildConfig.DEBUG) DebugLevel.VERBOSE.level else DebugLevel.INFO.level


    fun v(verbose: String) {
        v(TAG, verbose)
    }

    /**
     * 打印普通信息
     */
    fun v(tag: String, verbose: String) {
        if (DEBUG_MODE > DebugLevel.VERBOSE.level) {
            return
        }
        Log.v(tag, verbose)
    }

    fun d(debug: String) {
        d(TAG, debug)
    }

    /**
     * 打印调试信息
     */
    fun d(tag: String, debug: String) {
        if (DEBUG_MODE > DebugLevel.DEBUG.level) {
            return
        }
        Log.d(tag, debug)
    }

    fun i(info: String) {
        i(TAG, info)
    }

    /**
     * 打印说明信息
     */
    fun i(tag: String, info: String) {
        if (DEBUG_MODE > DebugLevel.INFO.level) {
            return
        }
        Log.i(tag, info)
    }

    fun w(waring: String) {
        w(TAG, waring)
    }

    /**
     * 打印警告信息
     */
    fun w(tag: String, waring: String) {
        if (DEBUG_MODE > DebugLevel.WARING.level) {
            return
        }
        Log.w(tag, waring)
    }

    fun e(error: String) {
        e(TAG, error)
    }

    /**
     * 打印错误信息
     */
    fun e(tag: String, error: String) {
        if (DEBUG_MODE > DebugLevel.ERROR.level) {
            return
        }
        Log.e(tag, error)
    }

    /**
     * 调试信息等级枚举
     *
     * @param level 等级值，依次为VERBOSE<DEBUG<INFO<WARING<ERROR
     */
    enum class DebugLevel(val level: Int) {
        /**
         * 普通消息
         */
        VERBOSE(2),

        /**
         * 调试信息
         */
        DEBUG(3),

        /**
         * 说明信息
         */
        INFO(4),

        /**
         * 警告信息
         */
        WARING(5),

        /**
         * 错误信息
         */
        ERROR(6),

        /**
         * 不要任何信息
         */
        NOT_ANY(9);

    }

}