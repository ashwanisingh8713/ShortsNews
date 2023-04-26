package com.ns.shortsnews.utils

import android.util.Patterns
import java.util.regex.Pattern

object Validation {

    const val PREF_USERNAME = "userName"
    const val PREF_USER_EMAIL = "userEmail"
    const val PREF_USER_TOKEN = "userToken"
    const val PREF_IS_USER_LOGGED_IN = "isLogged"
    fun validateEmail(email:String):Boolean{
        if (email.isEmpty()){
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        } else{
            return true
        }
    }

    fun validateOtp(otp:String):Boolean{
        return otp.isNotEmpty()
    }
}