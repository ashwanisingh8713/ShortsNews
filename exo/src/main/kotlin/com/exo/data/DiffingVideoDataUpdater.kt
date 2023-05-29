package com.exo.data

import android.util.Log
import com.exo.players.currentMediaItems
import com.github.difflib.DiffUtils
import com.github.difflib.patch.AbstractDelta
import com.github.difflib.patch.DeltaType
import com.github.difflib.patch.Patch
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.player.models.VideoData
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


/**
 * When new videos come in from VideoDataRepository or the UI's state has been updated without a
 * change to video data, there can't simply be a clearing of all current videos on ExoPlayer and then
 * adding the latest list of videos. That would disrupt the active video and be a janky UX. Instead,
 * use diffing to figure out what changed and only insert/delete/update those differences.
 */
internal class DiffingVideoDataUpdater(
    private val diffingContext: CoroutineContext
) : VideoDataUpdater {

    companion object {
        val adTagUri = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator="
    }

    override suspend fun update(player: ExoPlayer, incoming: List<VideoData>) {
        val oldMediaItems = player.currentMediaItems
        val newMediaItems = incoming.toMediaItems()

        val patch: Patch<MediaItem> = withContext(diffingContext) {
            DiffUtils.diff(oldMediaItems, newMediaItems)
        }
        patch.deltas.forEach { delta: AbstractDelta<MediaItem> ->
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (delta.type) {
                DeltaType.CHANGE -> {
                    delta.delete(player)
                    delta.insert(player)
                }
                DeltaType.DELETE -> delta.delete(player)
                DeltaType.INSERT -> delta.insert(player)
                DeltaType.EQUAL -> {

                } // Nothing to do here
            }
        }
    }

    private fun AbstractDelta<MediaItem>.delete(player: Player) {
        Log.i("Conv_TIME", "Diffing - delete : ${target.position}")
        player.removeMediaItems(target.position, target.position + source.lines.size)
    }

    private fun AbstractDelta<MediaItem>.insert(player: Player) {
        Log.i("Conv_TIME", "Diffing - insert : ${target.position}")
        val position = target.position
        val mediaItems = target.lines
        val mediaSources = mediaItems.toMediaSources()

        player.addMediaItems(position, mediaItems)
    }

    private fun List<VideoData>.toMediaItems(): List<MediaItem> {
        return map { videoData ->
            val mediaItemBuilder = MediaItem.Builder()
                .setMediaId(videoData.id)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(videoData.id)
                        .build()
                )
                .setUri(videoData.mediaUri)
                /*.setAdsConfiguration(
                    MediaItem.AdsConfiguration.Builder(Uri.parse(adTagUri)).setAdsId(videoData.id)
                        .build()
                )*/

            /*if(videoData.adTagUri.isNotEmpty()) {
                mediaItemBuilder.setAdsConfiguration(
                    MediaItem.AdsConfiguration.Builder(Uri.parse(videoData.adTagUri)).setAdsId(videoData.id)
                        .build()
                )
            }*/
            val mediaItem = mediaItemBuilder.build()
            mediaItem
        }
    }

    private fun List<MediaItem>.toMediaSources(): List<MediaSource> {
        return map { mediaItem ->
            val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            val mediaSource = DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
            mediaSource
        }
    }

    /*private fun tt(dashUrl: String) {
        val manifestUri = Uri.parse(dashUrl)
        val dataSourceFactory = MediaSource.Factory(
            diffingContext,
            DefaultHttpDataSource.Factory()
                .setUserAgent("ShortsVideo")
        )
    }*/
}
