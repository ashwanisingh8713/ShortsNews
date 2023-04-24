package com.ns.shortsnews.utils

import android.util.Patterns
import java.util.regex.Pattern

object Validation {
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