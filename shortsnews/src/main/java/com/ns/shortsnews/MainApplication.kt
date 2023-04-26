package com.ns.shortsnews

import android.app.Application
import com.ns.shortsnews.user.data.di.AppModule
import com.ns.shortsnews.user.data.di.NetworkModule
import com.ns.shortsnews.utils.PrefUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // declare used Android context
            androidContext(this@MainApplication)
            // declare modules
            modules(AppModule, NetworkModule)

            androidLogger(Level.DEBUG)
        }

        }

}