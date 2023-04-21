package com.ns.shortsnews.user.data.models

/**
 * Created by Ashwani Kumar Singh on 21,April,2023.
 */


sealed class BaseResult


data class OTPResult(val name: String,): BaseResult()

data class ProfileResult(val name: String,): BaseResult()

data class RegistrationResult(val name: String,): BaseResult()