package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.domain.models.*
import okhttp3.RequestBody


interface UserDataRepository {
    suspend fun getUserRegistration(data:Map<String, String>): RegistrationResult
    suspend fun getValidateOtpData(otp:Map<String, String>): OTPResult
    suspend fun getUserProfileData(): ProfileResult

    suspend fun getChannelsData():ChannelsDataResult

    suspend fun getChannelVideoData(channelId:String): VideoDataResponse
    suspend fun getLanguagesData():LanguagesResult

    //Likes list for profile screen
    suspend fun getBookmarksData(): VideoDataResponse

    // Update profile
    suspend fun getUpdateProfileData(data:RequestBody):StatusResult
}