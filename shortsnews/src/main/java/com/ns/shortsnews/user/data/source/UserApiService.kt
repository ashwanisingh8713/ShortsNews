package com.ns.shortsnews.user.data.source

import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.models.UserChoiceResult
import com.ns.shortsnews.user.domain.models.RegistrationResult
import com.ns.shortsnews.user.domain.models.VideoCategoryResult
import retrofit2.http.*

/**
 * Created by Ashwani Kumar Singh on 07,February,2023.
 */
interface UserApiService {

    @POST("send-email-otp")
    suspend fun getRegistration(@Body registrationData:Map<String, String>): RegistrationResult

    @POST("verify-email-otp")
    suspend fun getValidateOtp(@Body otpData:Map<String, String>): OTPResult

    @GET("explore-home")
    suspend fun getUserChoice(): UserChoiceResult

    @GET("categories")
    suspend fun getVideoCategory(): VideoCategoryResult

}