package com.videopager.data

import com.player.models.VideoData
import com.videopager.vm.VideoPagerViewModel
import kotlinx.coroutines.flow.Flow

interface VideoDataRepository {
    fun videoData(requestType: String): Flow<List<VideoData>>

    fun like(videoId: String, position: Int):Flow<Triple<String, Boolean, Int>>
    fun follow(videoId: String):Flow<String>
    fun comment(videoId: String, position: Int):Flow<Triple<String,Comments, Int>>
    fun getVideoInfo(videoId: String, position: Int):Flow<Pair<VideoInfo, Int>>
    fun getPostComment(videoId:String, comment:String, position:Int): Flow<Pair<PostComment, Int>>
}
