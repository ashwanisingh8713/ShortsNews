package com.ns.shortsnews.user.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Ashwani Kumar Singh on 21,April,2023.
 */


@JsonClass(generateAdapter = true)
data class ProfileResult(
    val data: ProfileData,
    val status:Boolean
)

@JsonClass(generateAdapter = true)
data class ProfileData(
    val user_id:String,
    val email:String,
    val name:String,
    val last_logged_in:String,
    val image:String
)
@JsonClass(generateAdapter = true)
data class OTPResult(
    @Json(name = "data")
    val `data`: OtpValidationData? = null,
    val status: Boolean,
    val msg: String
)
@JsonClass(generateAdapter = true)
data class OtpValidationData(
    val access_token: String,
    val email: String,
    val name: String,
    val first_time_user: Boolean
)

@JsonClass(generateAdapter = true)
data class UserChoiceResult(val name: String)
@JsonClass(generateAdapter = true)
data class RegistrationResult(
    val `data`: RegistrationData? = null,
    val status: Boolean,
    val msg: String
)
@JsonClass(generateAdapter = true)
data class RegistrationData(
    val OTP_id: Int,
    val length: Int,
    val email: String
)

@JsonClass(generateAdapter = true)
data class VideoCategoryResult(
    val data: List<VideoCategory>,
    val status: Boolean
)
@JsonClass(generateAdapter = true)
data class VideoCategory(
    val id: String,
    val name: String,
    val selected:Boolean = false,
    val optionSelected:Boolean = false,
)