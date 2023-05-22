package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.domain.models.*


interface UserDataRepository {
    suspend fun getUserRegistration(data:Map<String, String>): RegistrationResult
    suspend fun getValidateOtpData(otp:Map<String, String>): OTPResult
    suspend fun getUserProfileData(): ProfileResult

    suspend fun getChannelsData():ChannelsDataResult

    suspend fun geChannelVideoData(channelId:String): ChannelVideoDataResult
    suspend fun getLanguagesData():LanguagesResult

    //Likes list for profile screen
    suspend fun getLikesData():LikesResult
}