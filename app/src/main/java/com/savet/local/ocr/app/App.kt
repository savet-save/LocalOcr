package com.savet.local.ocr.app

import android.app.Application
import android.content.Context
import com.savet.local.baselibrary.utils.ToastUtils

class App : Application() {
    companion object {
        private lateinit  var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        ToastUtils.init(this)
    }

    fun getContext() : Context {
        return INSTANCE.getContext()
    }

}