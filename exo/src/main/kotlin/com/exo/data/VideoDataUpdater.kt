package com.exo.data

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.player.models.VideoData

internal interface VideoDataUpdater {
    suspend fun update(player: ExoPlayer, incoming: List<VideoData>)
}
