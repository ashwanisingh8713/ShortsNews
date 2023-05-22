package com.ns.shortsnews.user.data.repository

import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.user.domain.models.*
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserDataRepositoryImpl constructor(private val apiService: UserApiService): UserDataRepository {
    override suspend fun getUserRegistration(data:Map<String, String>): RegistrationResult {
        return apiService.getRegistration(data)
    }

    override suspend fun getValidateOtpData(otpData:Map<String, String>): OTPResult {
        return apiService.getValidateOtp(otpData)
    }

    override suspend fun getUserProfileData(): ProfileResult {
       return apiService.getUserProfile()
    }

    override suspend fun getChannelsData(): ChannelsDataResult {
        return apiService.getChannelData()
    }

    override suspend fun geChannelVideoData(channelId: String): ChannelVideoDataResult {
        return apiService.getChannelVideoData(channelId)
    }

    override suspend fun getLanguagesData(): LanguagesResult {
        return  apiService.getLanguagesData()
    }
    //Likes list for profile screen
    override suspend fun getLikesData(): LikesResult {
        return apiService.getLikesData()
    }



}