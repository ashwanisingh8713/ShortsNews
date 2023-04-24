package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.data.models.OTPResult
import com.ns.shortsnews.user.data.models.ProfileResult
import com.ns.shortsnews.user.data.models.RegistrationResult


interface UserDataRepository {
    suspend fun getUserRegistration(emailId:String): RegistrationResult
    suspend fun getValidateOtpData(otp:String): OTPResult
    suspend fun getProfileData(): ProfileResult
}