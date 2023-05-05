package com.videopager.data

import android.content.Context
import com.player.models.VideoData
import kotlinx.coroutines.flow.Flow

interface VideoDataRepository {
    suspend fun videoData(requestType: String, context: Context): Flow<MutableList<VideoData>>

    fun like(videoId: String, position: Int):Flow<Triple<String, Boolean, Int>>
    fun follow(channel_id: String, position: Int):Flow<Pair<Following, Int>>
    fun comment(videoId: String, position: Int):Flow<Triple<String,Comments, Int>>
    fun getVideoInfo(videoId: String, position: Int):Flow<Pair<VideoInfo, Int>>
    fun getPostComment(videoId:String, comment:String, position:Int): Flow<Pair<PostComment, Int>>

}
