package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.data.models.BaseResult
import com.ns.shortsnews.user.data.models.ProfileData
import com.ns.shortsnews.user.data.models.User
import com.squareup.moshi.Json

interface UserDataRepository {
    suspend fun getUserRegistration(emailId:String): BaseResult
    suspend fun getValidateOtpData(otp:String): BaseResult
    suspend fun getProfileData(): BaseResult
}