package com.savet.local.ocr.app

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        private lateinit  var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    fun getContext() : Context {
        return INSTANCE.getContext()
    }

}