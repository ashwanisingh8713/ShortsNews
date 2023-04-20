package com.exo.players

import android.content.Context
import com.exo.data.DiffingVideoDataUpdater
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.player.players.AppPlayer
import kotlinx.coroutines.Dispatchers

class ExoAppPlayerFactory(context: Context) : AppPlayer.Factory {
    // Use application context to avoid leaking Activity.
    private val appContext = context.applicationContext

    override fun create(config: AppPlayer.Factory.Config): AppPlayer {
        val exoPlayer = ExoPlayer.Builder(appContext)
            .build()
            .apply {
                if (config.loopVideos) {
                    loopVideos()
                }
            }
        val updater = DiffingVideoDataUpdater(Dispatchers.Default)
        return ExoAppPlayer(exoPlayer, updater)
    }

    companion object {
        lateinit var simpleCache: SimpleCache
        const val exoPlayerCacheSize: Long = 90 * 1024 * 1024
        lateinit var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor
        lateinit var exoDatabaseProvider: ExoDatabaseProvider
    }

    fun cachingInit() {
        leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        exoDatabaseProvider = ExoDatabaseProvider(appContext)
        simpleCache = SimpleCache(appContext.cacheDir, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
    }
}
