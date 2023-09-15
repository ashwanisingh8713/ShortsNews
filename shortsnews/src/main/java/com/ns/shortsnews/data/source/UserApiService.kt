package com.ns.shortsnews.data.source

import com.ns.shortsnews.domain.models.*
import com.videopager.data.Following
import com.videopager.data.VideoInfo
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

    @GET("videos")
    suspend fun getChannelVideoData(@Query("channel")channel:String): VideoDataResponse

    @GET("languages")
    suspend fun getLanguagesData(): LanguagesResult
    //Likes list for profile screen
    @GET("my-bookmarks")
    suspend fun getBookmarksData(): VideoDataResponse

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

}