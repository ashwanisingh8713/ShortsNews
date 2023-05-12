package com.exo.players

import android.content.Context
import com.exo.data.DiffingVideoDataUpdater
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.player.players.AppPlayer
import kotlinx.coroutines.Dispatchers


class ExoAppPlayerFactory(context: Context, private val cache: SimpleCache) : AppPlayer.Factory {
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
