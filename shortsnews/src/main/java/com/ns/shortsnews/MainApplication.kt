package com.ns.shortsnews

import android.app.Application
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class MainApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // declare used Android context
          KoinApplication.init()
            // declare modules
            modules()
        }

        }

}