package com.ns.shortsnews.user.data.repository

import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.models.RegistrationResult
import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.squareup.moshi.Json

class UserDataRepositoryImpl(private val apiService: UserApiService): UserDataRepository {
    override suspend fun getUserRegistration(emailId: String): RegistrationResult {
        return apiService.getRegistration()
    }

    override suspend fun getValidateOtpData(otp: String): OTPResult {
        return apiService.getValidateOtp()
    }

    override suspend fun getProfileData(): ProfileResult {
       return apiService.getUserProfile()
    }
}