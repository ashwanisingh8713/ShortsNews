package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.domain.models.ProfileData
import com.ns.shortsnews.user.domain.models.User
import com.squareup.moshi.Json

interface UserDataRepository {
    suspend fun getUserRegistration(emailId:String): Json
    suspend fun getValidateOtpData(otp:String): Json
    suspend fun getProfileData(): Json
}