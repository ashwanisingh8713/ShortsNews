package com.ns.shortsnews.data.source

import com.google.gson.JsonObject
import com.ns.shortsnews.domain.models.*
import com.videopager.data.BookMarkResult
import com.videopager.data.Comments
import com.videopager.data.Following
import com.videopager.data.PostComment
import com.videopager.data.VideoInfo
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by Ashwani Kumar Singh on 07,February,2023.
 */
interface UserApiService {

    @POST("send-email-otp")
    suspend fun getRegistration(@Body registrationData:Map<String, String>): RegistrationResult

    @POST("verify-email-otp")
    suspend fun getValidateOtp(@Body otpData:Map<String, String>): OTPResult

    @GET("my-profile")
    suspend fun getUserProfile(): ProfileResult

    @GET("my-channels")
    suspend fun getChannelData(): ChannelsDataResult

    @GET("categories")
    suspend fun getVideoCategory(@Query("languages")languages:String): VideoCategoryResult

    @POST("store-categories")
    suspend fun updateCategory(@Body categories:Any): UpdateCategories

    @GET("videos")
    suspend fun getChannelVideoData(@Query("channel")channel:String): VideoDataResponse
    @GET("videos")
    suspend fun getChannelVideoDataP(@Query("channel")channel:String, @Query("page")page:Int, @Query("perPage")perPage:Int): VideoDataResponse

    @GET("languages")
    suspend fun getLanguagesData(): LanguagesResult

    @GET("my-bookmarks")
    suspend fun getBookmarksData(): VideoDataResponse

    @GET("my-bookmarks")
    suspend fun getBookmarksDataP(@Query("page")page:Int, @Query("perPage")perPage:Int): VideoDataResponse

    @POST("update-profile")
    suspend fun getUpdateProfile(@Body body: RequestBody): StatusResult

    @GET("delete-profile")
    suspend fun getDeleteProfile():StatusResult

    @GET("channel-info/{channel_id}")
    suspend fun getChannelInfo(@Path("channel_id") channel_id: String): ChannelInfo

    @GET("follow-unfollow-channel/{channel_id}")
    suspend fun follow(@Path("channel_id") channel_id: String): Following

    @POST("send-device-token")
    suspend fun sendFCMToken(@Body body: Map<String, String>):StatusResult

    @GET("user-selections")
    suspend fun getUserSelections():UserSelectionResult

    //////////

    @GET("videos")
    suspend fun getShortsVideos(@Query("category") category: String, @Query("page") page: Int,
                                @Query("perPage") perPage: Int, @Query("languages")languages:String): VideoDataResponse

    @GET("videos")
    suspend fun getChannelVideos(@Query("channel") category: String): VideoDataResponse

    @GET("notifications")
    suspend fun getNotificationVideos(): VideoDataResponse

    @GET("my-bookmarks")
    suspend fun getBookmarkVideos(@Query("page") page: Int, @Query("perPage") perPage: Int): VideoDataResponse

    @GET("like-unlike-video/{video_id}")
    suspend fun like(@Path("video_id") videoId: String): LikeUnlike

    @GET("bookmark-video/{video_id}")
    suspend fun save(@Path("video_id") videoId: String): BookMarkResult

    @GET("get-comments/{video_id}")
    suspend fun comment(@Path("video_id") videoId: String): Comments

    @GET("video-info/{video_id}")
    suspend fun getVideoInfo(@Path("video_id") videoId: String): VideoInfo

    @GET("video-info/{video_id}")
    suspend fun getVideoInfoFlow(@Path("video_id") videoId: String): Flow<VideoInfo>

    @POST("comment-video/{video_id}")
    suspend fun getPostComment(
        @Path("video_id") videoId: String,
        @Body comment: Map<String, String>
    ): PostComment

}