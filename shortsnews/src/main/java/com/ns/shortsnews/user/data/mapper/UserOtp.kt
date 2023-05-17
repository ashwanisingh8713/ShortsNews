package com.ns.shortsnews.user.data.mapper

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
data class UserOtp(
    val status: Boolean = false,
    val msg: String = "",
    val access_token: String = "",
    val email: String = "",
    val name: String? = "",
    val first_time_user: Boolean = false
) {
    fun mapper(
        status: Boolean,
        msg: String,
        access_token: String,
        email: String,
        name: String?,
        first_time_user: Boolean
    ): UserOtp {
        return UserOtp(
            status = status,
            msg = msg,
            access_token = access_token,
            email = email,
            name = name,
            first_time_user = first_time_user
        )
    }
}
