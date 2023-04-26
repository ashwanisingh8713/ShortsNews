package com.ns.shortsnews.user.data.repository

import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.UserChoiceResult
import com.ns.shortsnews.user.domain.models.RegistrationResult
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import org.koin.core.Koin

class UserDataRepositoryImpl constructor(private val apiService: UserApiService): UserDataRepository {
    override suspend fun getUserRegistration(data:Map<String, String>): RegistrationResult {
        return apiService.getRegistration(data)
    }

    override suspend fun getValidateOtpData(otpData:Map<String, String>): OTPResult {
        return apiService.getValidateOtp(otpData)
    }

    override suspend fun getUserChoiceData(): UserChoiceResult {
       return apiService.getUserChoice()
    }
}