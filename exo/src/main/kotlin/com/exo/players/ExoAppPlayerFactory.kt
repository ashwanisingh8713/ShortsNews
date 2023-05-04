package com.exo.players

import android.content.Context
import android.net.Uri
import android.util.Log
import com.exo.data.DiffingVideoDataUpdater
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.player.players.AppPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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





}
