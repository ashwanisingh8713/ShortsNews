package com.ns.shortsnews.user.data.source

import com.squareup.moshi.Json
import retrofit2.http.*

/**
 * Created by Ashwani Kumar Singh on 07,February,2023.
 */
interface UserApiService {

    @GET("/explore-home")
    suspend fun getRegistration(): Json

    @GET("/explore-home")
    suspend fun getValidateOtp(): Json

    @GET("/explore-home")
    suspend fun getUserProfile(): Json

}