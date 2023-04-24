package com.ns.shortsnews.user.data.source

import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.models.RegistrationResult
import com.squareup.moshi.Json
import retrofit2.http.*

/**
 * Created by Ashwani Kumar Singh on 07,February,2023.
 */
interface UserApiService {

    @POST("send-email-otp")
    suspend fun getRegistration(): RegistrationResult

    @POST("verify-email-otp")
    suspend fun getValidateOtp(): OTPResult

    @GET("explore-home")
    suspend fun getUserProfile(): ProfileResult

}