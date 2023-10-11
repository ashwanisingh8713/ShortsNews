package com.ns.shortsnews

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ns.shortsnews.data.di.AppModule
import com.ns.shortsnews.data.di.NetworkModule
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.utils.AppPreference
import com.rommansabbir.networkx.NetworkXLifecycle
import com.rommansabbir.networkx.NetworkXProvider
import com.rommansabbir.networkx.SmartConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.Executors


class MainApplication:Application(), Configuration.Provider {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val cacheSize: Long = 1*1024 * 1024 * 1024 //1 Gb for cache

    private val activeActivityCallbacks = ActiveActivityLifecycleCallbacks()

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
        NetworkXProvider.enable(SmartConfig(this, false, NetworkXLifecycle.Application))
        registerActivityLifecycleCallbacks(activeActivityCallbacks)

        ShortsDatabase.getInstance(context)
        firebaseAnalytics = Firebase.analytics
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

    fun getActiveActivity(): Activity? = activeActivityCallbacks.getActiveActivity()


    override fun onTerminate() {
        unregisterActivityLifecycleCallbacks(activeActivityCallbacks)
        super.onTerminate()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            // Defines a thread pool with 2 threads.
            // This can be N (typically based on the number of cores on the device)
            .setExecutor(Executors.newFixedThreadPool(4))
            .build()
    }
}

class ActiveActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    private var activeActivity: Activity? = null

    fun getActiveActivity(): Activity? = activeActivity

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activeActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity === activeActivity) {
            activeActivity = null
        }
    }
}