package com.ns.shortsnews

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ns.shortsnews.data.di.AppModule
import com.ns.shortsnews.data.di.NetworkModule
import com.ns.shortsnews.utils.AppPreference
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.Executors


class MainApplication:Application(), ImageLoaderFactory, Configuration.Provider {

    private val cacheSize: Long = 1*1024 * 1024 * 1024 //1 Gb for cache

    init {
        instance = this
    }
    companion object {
        var instance: MainApplication? = null
        lateinit var cache: SimpleCache


        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
        fun getAppWorkManager(): WorkManager {
            return WorkManager.getInstance(instance!!)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context = applicationContext()
        AppPreference.init(context)
        startKoin {
            // declare used Android context
            androidContext(this@MainApplication)
            // declare modules
            modules(AppModule, NetworkModule)

            androidLogger(Level.DEBUG)
        }

        cache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(cacheSize), StandaloneDatabaseProvider(this))

    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            // Defines a thread pool with 2 threads.
            // This can be N (typically based on the number of cores on the device)
            .setExecutor(Executors.newFixedThreadPool(4))
            .build()
    }



}