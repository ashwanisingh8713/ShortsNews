package com.ns.shortsnews.data.mapper

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
data class UserRegistration(
    val status: Boolean = false,
    val msg: String = "",
    val OTP_id: Int = 0,
    val length: Int = 0,
    val email: String = "",
    val is_registered:Boolean = false
) {
    fun mapper(status: Boolean, msg: String, OTP_id: Int, length: Int,email: String, isUserRegistered:Boolean): UserRegistration {
        return UserRegistration(status = status, msg=msg, OTP_id=OTP_id, length=length, email=email, is_registered = isUserRegistered)
    }
}
