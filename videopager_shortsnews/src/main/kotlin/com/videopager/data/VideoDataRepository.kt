package com.videopager.data

import com.player.models.VideoData
import kotlinx.coroutines.flow.Flow

interface VideoDataRepository {
    fun videoData(requestType: String): Flow<List<VideoData>>
}
