package com.ns.shortsnews.user.data.source

import com.ns.shortsnews.user.domain.models.*
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
    suspend fun getChannelData():ChannelsDataResult

    @GET("categories")
    suspend fun getVideoCategory(): VideoCategoryResult

    @GET("videos")
    suspend fun getChannelVideoData(@Query("channel")channel:String):ChannelVideoDataResult

    @GET("languages")
    suspend fun getLanguagesData():LanguagesResult
    //Likes list for profile screen
    @GET("my-bookmarks")
    suspend fun getBookmarksData(): VideoDataResponse

}