package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.models.RegistrationResult


interface UserDataRepository {
    suspend fun getUserRegistration(emailId:String): RegistrationResult
    suspend fun getValidateOtpData(otp:String): OTPResult
    suspend fun getProfileData(): ProfileResult
}