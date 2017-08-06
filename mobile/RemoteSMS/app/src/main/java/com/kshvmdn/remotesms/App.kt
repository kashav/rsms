package com.kshvmdn.remotesms

import android.app.Application
import android.content.Context

class App : Application() {
    val TAG = "App"

    // TODO: Figure out a better way to do this. Apparently storing Context as a static field
    // leads to memory leaks (why?).
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}