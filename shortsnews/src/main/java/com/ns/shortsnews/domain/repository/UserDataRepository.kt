package com.ns.shortsnews.domain.repository

import com.ns.shortsnews.domain.models.*
import com.videopager.data.Following
import okhttp3.RequestBody


interface UserDataRepository {
    suspend fun getUserRegistration(data:Map<String, String>): RegistrationResult
    suspend fun getValidateOtpData(otp:Map<String, String>): OTPResult
    suspend fun getUserProfileData(): ProfileResult

    suspend fun getChannelsData(): ChannelsDataResult

    suspend fun getChannelVideoData(channelId:String): VideoDataResponse
    suspend fun getLanguagesData(): LanguagesResult

    //Likes list for profile screen
    suspend fun getBookmarksData(): VideoDataResponse

    // Update profile
    suspend fun getUpdateProfileData(data:RequestBody): StatusResult

    //Delete Profile
    suspend fun getDeleteProfile():StatusResult

    //GetChannelInfo
    suspend fun getChannelInfo(channelId: String):ChannelInfo

    suspend fun getFollowUnfollow(channelId: String):Following

    suspend fun getNotificationTokenStatus(data: Map<String, String>):StatusResult

    suspend fun getUserSelections():UserSelectionResult
}