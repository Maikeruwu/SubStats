package com.maikeruwu.substats

import android.app.Application
import android.content.Context

class SubStatsApplication : Application() {

    companion object {
        private lateinit var instance: SubStatsApplication

        val appContext: Context
            get() = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}