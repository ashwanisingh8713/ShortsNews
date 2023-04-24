package com.ns.shortsnews.user.data.mapper

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
data class UserOtp(
    val status: Boolean,
    val msg: String,
    val access_token: String,
    val email: String,
    val name: Boolean,
    val first_time_user: Boolean
)
