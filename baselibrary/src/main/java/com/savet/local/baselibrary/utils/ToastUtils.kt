package com.savet.local.baselibrary.utils

import android.app.Application
import android.widget.Toast

object ToastUtils {
    private lateinit var context: Application

    private var toast: Toast? = null

    fun init(app: Application) {
        context = app
    }

    /**
     * 以指定消息显示Toast
     *
     * @param msg 消息
     */
    fun showToast(msg: CharSequence) {
        toast = if (toast == null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.cancel()
            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        }
        toast!!.show()
    }

}