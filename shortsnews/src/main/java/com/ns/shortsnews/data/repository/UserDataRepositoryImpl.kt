package com.ns.shortsnews.data.repository

import com.ns.shortsnews.domain.models.*
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.repository.UserDataRepository
import com.videopager.data.Following
import okhttp3.RequestBody

class UserDataRepositoryImpl constructor(private val apiService: UserApiService):
    UserDataRepository {
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

    override suspend fun getDeleteProfile(): StatusResult {
      return apiService.getDeleteProfile()
    }

    override suspend fun getChannelInfo(channelId: String): ChannelInfo {
        return apiService.getChannelInfo(channelId)
    }

    override suspend fun getFollowUnfollow(channelId: String): Following {
       return apiService.follow(channelId)
    }

    override suspend fun getNotificationTokenStatus(data: Map<String, String>): StatusResult {
        return apiService.sendFCMToken(data)
    }

    override suspend fun getUserSelections(): UserSelectionResult {
        return apiService.getUserSelections()
    }

}