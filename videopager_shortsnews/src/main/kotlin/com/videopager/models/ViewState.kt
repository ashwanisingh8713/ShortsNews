package com.videopager.models

import com.player.models.VideoData
import com.player.players.AppPlayer

data class ViewState(
    val appPlayer: AppPlayer? = null,
    val attachPlayer: Boolean = false,
    var page: Int = 0,
    val showPlayer: Boolean = false,
    val youtubeUriError: Boolean = false,
    val videoData: MutableList<VideoData>? = null,
)
