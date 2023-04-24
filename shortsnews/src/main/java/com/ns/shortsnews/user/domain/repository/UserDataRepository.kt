package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.data.models.BaseResult

interface UserDataRepository {
    suspend fun getUserRegistration(emailId:String): BaseResult
    suspend fun getValidateOtpData(otp:String): BaseResult
    suspend fun getProfileData(): BaseResult
}