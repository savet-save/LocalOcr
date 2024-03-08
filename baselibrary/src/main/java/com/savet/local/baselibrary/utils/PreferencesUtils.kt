package com.savet.local.baselibrary.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * 用于方便使用SharedPreferences
 *
 * 需要执行[init]之后才能使用其他函数
 */
object PreferencesUtils {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var initSuccess: Boolean = false

    /**
     * 初始化
     *
     * @param context context
     */
    fun init(context: Context) {
        if (initSuccess) { // 避免重复初始化
            return
        }
        sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        initSuccess = true
    }

    fun setString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun setInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setFloat(key: String, value: Float) {
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun setLong(key: String, value: Long) {
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    /**
     * 清空内容值
     */
    fun clearPreferences() {
        editor.clear()
        editor.apply()
    }
}