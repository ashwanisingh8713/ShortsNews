package com.player.players

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.player.models.PlayerState
import com.player.models.VideoData
import kotlinx.coroutines.flow.Flow

// Abstract the underlying player to facilitate testing and hide player implementation details.
interface AppPlayer {
    val currentPlayerState: PlayerState

    val player: ExoPlayer

    suspend fun setUpWith(videoData: List<VideoData>, playerState: PlayerState?)

    suspend fun resumeSetupWith(videoData: List<VideoData>, playerState: PlayerState?)
    fun onPlayerRendering(): Flow<Unit>
    fun errors(): Flow<Throwable>
    fun playMediaAt(position: Int)
    fun play()
    fun pause()
    fun release()

    fun onTracksChanged(): Flow<Unit>
    fun onTimelineChanged(): Flow<Unit>
    fun onMediaItemTransition(): Flow<MediaItem>
    fun onPlaybackStateChanged(): Flow<Int>



    interface Factory {
        fun create(config: Config, playerView: StyledPlayerView): AppPlayer

        data class Config(
            val loopVideos: Boolean = false
        )
    }
}
