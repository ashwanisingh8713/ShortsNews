package com.ns.shortsnews.user.data.models

import com.squareup.moshi.JsonClass

/**
 * Created by Ashwani Kumar Singh on 21,April,2023.
 */

@JsonClass(generateAdapter = true)
data class OTPResult(
    val data:OtpValidationData,
    val status: Boolean,
    val msg: String
)
@JsonClass(generateAdapter = true)
data class OtpValidationData(
    val access_token: String,
    val email: String,
    val name: Boolean,
    val first_time_user: Boolean
)

@JsonClass(generateAdapter = true)
data class ProfileResult(val name: String)
@JsonClass(generateAdapter = true)
data class RegistrationResult(
    val data: RegistrationData,
    val status: Boolean,
    val msg: String
)
@JsonClass(generateAdapter = true)
data class RegistrationData(
    val OTP_id: Int,
    val length: Int,
    val email: String
)