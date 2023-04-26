package com.videopager.data

import com.player.models.VideoData
import kotlinx.coroutines.flow.Flow

interface VideoDataRepository {
    fun videoData(requestType: String): Flow<List<VideoData>>

    fun like(videoId: String):Flow<String>
    fun follow(videoId: String):Flow<String>
    fun comment(videoId: String):Flow<String>
}
