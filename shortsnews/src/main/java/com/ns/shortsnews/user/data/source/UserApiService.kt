package com.ns.shortsnews.user.data.source

import com.ns.shortsnews.user.data.models.OTPResult
import com.ns.shortsnews.user.data.models.ProfileResult
import com.ns.shortsnews.user.data.models.RegistrationResult
import com.squareup.moshi.Json
import retrofit2.http.*

/**
 * Created by Ashwani Kumar Singh on 07,February,2023.
 */
interface UserApiService {

    @GET("/explore-home")
    suspend fun getRegistration(): RegistrationResult

    @GET("/explore-home")
    suspend fun getValidateOtp(): OTPResult

    @GET("/explore-home")
    suspend fun getUserProfile(): ProfileResult

}