package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.domain.models.ChannelsDataResult
import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.models.RegistrationResult


interface UserDataRepository {
    suspend fun getUserRegistration(data:Map<String, String>): RegistrationResult
    suspend fun getValidateOtpData(otp:Map<String, String>): OTPResult
    suspend fun getUserProfileData(): ProfileResult

    suspend fun getChannelsData():ChannelsDataResult
}