package com.exo.players

import android.util.Log
import android.widget.Toast
import com.exo.data.VideoDataUpdater
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException
import com.player.models.PlayerState
import com.player.models.VideoData
import com.player.players.AppPlayer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal class ExoAppPlayer(
    override val player: ExoPlayer,
    private val updater: VideoDataUpdater,
    private val currentMediaItemIndex: Int
) : AppPlayer {
    override val currentPlayerState: PlayerState get() = player.toPlayerState(currentMediaItemIndex)
    private var isPlayerSetUp = false

    override suspend fun setUpWith(videoData: List<VideoData>, playerState: PlayerState?) {
        /** Delegate video insertion, removing, moving, etc. to this [updater] */
        updater.update(player = player, incoming = videoData)

        // Player should only have saved state restored to it one time per instance of this class.
        if (!isPlayerSetUp) {
            setUpPlayerState(playerState)
            isPlayerSetUp = true
            Log.i("Ashwani", "setUpWith() :: true")
        }

        player.prepare()
    }

    private fun setUpPlayerState(playerState: PlayerState?) {
        val currentMediaItems = player.currentMediaItems

         // When restoring saved state, the saved media item might be not be in the player's current
         // collection of media items. In that case, the saved media item cannot be restored.
        val canRestoreSavedPlayerState = playerState != null
            && currentMediaItems.any { mediaItem -> mediaItem.mediaId == playerState.currentMediaItemId }

        val reconciledPlayerState = if (canRestoreSavedPlayerState) {
            requireNotNull(playerState)
        } else {
            PlayerState.INITIAL
        }

        val windowIndex = currentMediaItems.indexOfFirst { mediaItem ->
            mediaItem.mediaId == reconciledPlayerState.currentMediaItemId
        }
        if (windowIndex != -1) {
            player.seekTo(windowIndex, reconciledPlayerState.seekPositionMillis)
        }
        player.playWhenReady = reconciledPlayerState.isPlaying
    }

    // A signal that video content is immediately ready to play; any preview images
    // on top of the video can be hidden to reveal actual video playback underneath.
    override fun onPlayerRendering(): Flow<Unit> = callbackFlow {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                trySend(Unit)
            }
        }

        player.addListener(listener)

        awaitClose { player.removeListener(listener) }
    }

    override fun errors(): Flow<Throwable> = callbackFlow {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {

//                var err = error
//                var sourceException = (err as ExoPlaybackException).sourceException
//                Log.i("", "$err")
                trySend(error)
            }
        }
        player.addListener(listener)
        awaitClose { player.removeListener(listener) }
    }

    override fun onTracksChanged(): Flow<Unit> = callbackFlow {
        val listener = object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                trySend(Unit)
            }
        }
        player.addListener(listener)
        awaitClose { player.removeListener(listener) }
    }

    override fun onTimelineChanged(): Flow<Unit>  = callbackFlow {
        /*val listener = object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                trySend(Unit)
            }
        }
        player.addListener(listener)
        awaitClose { player.removeListener(listener) }*/
    }

    override fun onMediaItemTransition(): Flow<MediaItem> = callbackFlow {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (mediaItem != null && (reason == 1) ) {
                    trySend(mediaItem)
                }
            }
        }
        player.addListener(listener)
        awaitClose { player.removeListener(listener) }

    }

    override fun onPlaybackStateChanged(): Flow<Int> = callbackFlow {
        val listener = object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                trySend(playbackState)

//                if (playbackState == ExoPlayer.STATE_ENDED) {
//                    showControls();
//                    Toast.makeText(getApplicationContext(), "Playback ended", Toast.LENGTH_LONG).show();
//                }
//                else if (playbackState == ExoPlayer.STATE_BUFFERING)
//                {
//                    progressBar.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "Buffering..", Toast.LENGTH_SHORT).show();
//                }
//                else if (playbackState == ExoPlayer.STATE_READY)
//                {
//                    progressBar.setVisibility(View.INVISIBLE);
//                }
            }
        }
        player.addListener(listener)
        awaitClose { player.removeListener(listener) }
    }


    private fun Player.toPlayerState(currentMediaItemIndex: Int): PlayerState {
        return PlayerState(
            currentMediaItemId = currentMediaItem?.mediaId,
            currentMediaItemIndex = currentMediaItemIndex,
            seekPositionMillis = currentPosition,
            isPlaying = playWhenReady
        )
    }

    override fun playMediaAt(position: Int) {
        // Already playing media at this position; nothing to do
        if (player.currentMediaItemIndex == position && player.isPlaying) {
            return
        }

        player.seekToDefaultPosition(position)
        player.playWhenReady = true
        player.prepare() // Recover from any errors that may have happened at previous media positions
    }

    override fun play() {
        player.prepare() // Recover from any errors
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun release() {
        player.release()
    }
}
