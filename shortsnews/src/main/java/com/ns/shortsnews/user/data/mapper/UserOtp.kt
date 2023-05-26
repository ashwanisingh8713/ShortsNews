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
    val first_time_user: Boolean = false,
    val userProfileImage:String = "",
    val user_id:String = "",
    val age:String = "",
    val location:String = "",
) {
    fun mapper(
        status: Boolean,
        msg: String,
        access_token: String,
        email: String,
        name: String?,
        first_time_user: Boolean,
        userProfileImage: String,
        user_id: String,
        age: String,
        location: String
    ): UserOtp {
        return UserOtp(
            status = status,
            msg = msg,
            access_token = access_token,
            email = email,
            name = name,
            first_time_user = first_time_user,
            userProfileImage = userProfileImage,
            user_id = user_id,
            age = age,
            location = location
        )
    }
}
