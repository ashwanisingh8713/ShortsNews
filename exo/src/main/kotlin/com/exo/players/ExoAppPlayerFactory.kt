package com.exo.players

import android.content.Context
import com.exo.data.DiffingVideoDataUpdater
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.ext.ima.ImaServerSideAdInsertionMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.EventLogger
import com.player.players.AppPlayer
import kotlinx.coroutines.Dispatchers


class ExoAppPlayerFactory(context: Context, private val cache: SimpleCache) : AppPlayer.Factory {
    // Use application context to avoid leaking Activity.
    private val appContext = context.applicationContext
    var exoPlayer:ExoPlayer? = null

    override fun create(config: AppPlayer.Factory.Config, playerView: StyledPlayerView): AppPlayer {

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(appContext, videoTrackSelectionFactory)
        trackSelector.setParameters(
            trackSelector
                .buildUponParameters()
                .setAllowVideoMixedMimeTypeAdaptiveness(true))

        val renderersFactory =  DefaultRenderersFactory(appContext)
            .forceEnableMediaCodecAsynchronousQueueing()
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

        val maxBufferMs = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS
        val loadControl =  DefaultLoadControl.Builder()
            .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
            maxBufferMs,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
            .build();

        exoPlayer = ExoPlayer.Builder(appContext, renderersFactory)
            .setMediaSourceFactory(createMediaSourceFactory(playerView))
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
            .apply {
                if (config.loopVideos) {
                    loopVideos()
                }
            }

            exoPlayer?.addAnalyticsListener(EventLogger())

        val updater = DiffingVideoDataUpdater(Dispatchers.Default)
        return ExoAppPlayer(exoPlayer!!, updater)
    }

    private var serverSideAdsLoader: ImaServerSideAdInsertionMediaSource.AdsLoader? = null

    private fun buildCacheDataSourceFactory(): DataSource.Factory {
        val cache = cache
        val cacheSink = CacheDataSink.Factory()
            .setCache(cache)
        val upstreamFactory = DefaultDataSource.Factory(appContext, DefaultHttpDataSource.Factory().apply {
            setUserAgent("ShortsVideo")
//            setConnectTimeoutMs(3000)
//            setReadTimeoutMs(3000)
            setAllowCrossProtocolRedirects(true)
        })



        return CacheDataSource.Factory()
            .setCache(cache)
            .setCacheWriteDataSinkFactory(cacheSink)
            .setCacheReadDataSourceFactory(FileDataSource.Factory())
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private fun createMediaSourceFactory(playerView: StyledPlayerView):MediaSource.Factory {
        var mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        /*val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(mHttpDataSourceFactory)*/

        val cacheDataSourceFactory: DataSource.Factory = buildCacheDataSourceFactory()
//        val cacheDataSourceFactory: DataSource.Factory =  DemoUtil.getDataSourceFactory(appContext)

        val serverSideAdLoaderBuilder =
            ImaServerSideAdInsertionMediaSource.AdsLoader.Builder( appContext, playerView)

        serverSideAdsLoader = serverSideAdLoaderBuilder.build()

        val drmSessionManagerProvider = DefaultDrmSessionManagerProvider()
        drmSessionManagerProvider.setDrmHttpDataSourceFactory(null)

        /*return DefaultMediaSourceFactory(appContext)
            .setDataSourceFactory(cacheDataSourceFactory)
            .setDrmSessionManagerProvider(drmSessionManagerProvider)
            .setLocalAdInsertionComponents({ adsConfiguration: AdsConfiguration? ->
                getClientSideAdsLoader(
                    adsConfiguration!!
                )
            }, playerView)
            .setServerSideAdInsertionMediaSourceFactory(imaServerSideAdInsertionMediaSourceFactory)*/


        return HlsMediaSource.Factory(cacheDataSourceFactory)

    }


    // For ad playback only.
    private var clientSideAdsLoader: AdsLoader? = null
    private fun getClientSideAdsLoader(adsConfiguration: AdsConfiguration): AdsLoader? {
        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
        if (clientSideAdsLoader == null) {
            clientSideAdsLoader = ImaAdsLoader.Builder(appContext).build()
        }
        clientSideAdsLoader!!.setPlayer(exoPlayer)
        return clientSideAdsLoader
    }



}
