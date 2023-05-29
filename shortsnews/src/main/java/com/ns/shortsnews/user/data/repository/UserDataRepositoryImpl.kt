package com.ns.shortsnews.user.data.repository

import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.user.domain.models.*
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

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

    override suspend fun getChannelVideoData(channelId: String): VideoDataResponse {
        return apiService.getChannelVideoData(channelId)
    }

    override suspend fun getLanguagesData(): LanguagesResult {
        return  apiService.getLanguagesData()
    }
    //Likes list for profile screen
    override suspend fun getBookmarksData(): VideoDataResponse {
        return apiService.getBookmarksData()
    }

    override suspend fun getUpdateProfileData(data:RequestBody): StatusResult {
        return  apiService.getUpdateProfile(data)
    }
}