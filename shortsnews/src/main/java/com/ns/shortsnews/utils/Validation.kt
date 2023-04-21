package com.ns.shortsnews.utils

import android.util.Patterns
import java.util.regex.Pattern

object Validation {
    fun validateEmail(email:String):Boolean{
        return !(email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun validateOtp(otp:String):Boolean{
        return otp.isNotEmpty()
    }
}