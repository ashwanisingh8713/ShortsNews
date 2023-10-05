package com.videopager.data

import android.content.Context
import com.player.models.VideoData
import kotlinx.coroutines.flow.Flow

interface VideoDataRepository {
    suspend fun videoData(requiredId: String, context: Context, videoFrom: String, page: Int, perPage: Int, languages:String): Flow<MutableList<VideoData>>
    fun like(videoId: String, position: Int):Flow<Triple<String, Boolean, Int>>
    fun save(videoId: String, position: Int):Flow<Triple<String, Boolean, Int>>
    fun follow(channel_id: String, position: Int):Flow<Pair<Following, Int>>
    fun comment(videoId: String, position: Int):Flow<Triple<String,Comments, Int>>
    fun getVideoInfo(videoId: String, position: Int):Flow<Pair<VideoInfoData, Int>>
    fun getPostComment(videoId:String, comment:String, position:Int): Flow<Pair<PostComment, Int>>

}
