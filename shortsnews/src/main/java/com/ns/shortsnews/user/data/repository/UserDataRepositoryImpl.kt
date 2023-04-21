package com.ns.shortsnews.user.data.repository

import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.squareup.moshi.Json

class UserDataRepositoryImpl(private val apiService: UserApiService): UserDataRepository {
    override suspend fun getUserRegistration(emailId: String): Json {
        return apiService.getRegistration()
    }

    override suspend fun getValidateOtpData(otp: String): Json {
        return apiService.getValidateOtp()
    }

    override suspend fun getProfileData(): Json {
       return apiService.getUserProfile()
    }
}