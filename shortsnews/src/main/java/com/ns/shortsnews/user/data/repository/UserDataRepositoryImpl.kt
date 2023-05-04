package com.ns.shortsnews.user.data.repository

import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.models.RegistrationResult
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
}