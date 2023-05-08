package com.ns.shortsnews

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.preference.Preference
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ns.shortsnews.user.data.di.AppModule
import com.ns.shortsnews.user.data.di.NetworkModule
import com.ns.shortsnews.utils.PrefUtils
import com.ns.shortsnews.utils.Validation
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication:Application() {


    private val cacheSize: Long = 90 * 1024 * 1024

    init {
        instance = this
    }
    companion object {
        private var instance: MainApplication? = null
        lateinit var cache: SimpleCache

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context = applicationContext()
        PrefUtils.with(context, Validation.PREFERENCE_NAME, Context.MODE_PRIVATE)
        startKoin {
            // declare used Android context
            androidContext(this@MainApplication)
            // declare modules
            modules(AppModule, NetworkModule)

            androidLogger(Level.DEBUG)
        }

        cache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(cacheSize), StandaloneDatabaseProvider(this))
//        cache = SimpleCache(cacheDir, NoOpCacheEvictor(), StandaloneDatabaseProvider(this))

    }



}