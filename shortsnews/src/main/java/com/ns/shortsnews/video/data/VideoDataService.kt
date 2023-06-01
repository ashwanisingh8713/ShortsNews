package com.ns.shortsnews.video.data

import com.ns.shortsnews.domain.models.LikeUnlike
import com.ns.shortsnews.domain.models.VideoDataResponse
import com.videopager.data.*
import retrofit2.http.*

/**
 * Created by Ashwani Kumar Singh on 29,May,2023.
 */
interface VideoDataService {
    @GET("videos")
    suspend fun getShortsVideos(@Query("category") category: String, @Query("page") page: Int, @Query("perPage") perPage: Int): VideoDataResponse

    @GET("videos")
    suspend fun getChannelVideos(@Query("channel") category: String): VideoDataResponse

    @GET("my-bookmarks")
    suspend fun getBookmarkVideos(): VideoDataResponse

    @GET("like-unlike-video/{video_id}")
    suspend fun like(@Path("video_id") videoId: String): LikeUnlike

    @GET("bookmark-video/{video_id}")
    suspend fun save(@Path("video_id") videoId: String): BookMarkResult

    @GET("follow-unfollow-channel/{channel_id}")
    suspend fun follow(@Path("channel_id") channel_id: String): Following

    @GET("get-comments/{video_id}")
    suspend fun comment(@Path("video_id") videoId: String): Comments

    @GET("video-info/{video_id}")
    suspend fun getVideoInfo(@Path("video_id") videoId: String): VideoInfo

    @POST("comment-video/{video_id}")
    suspend fun getPostComment(
        @Path("video_id") videoId: String,
        @Body comment: Map<String, String>
    ): PostComment
}