package com.ns.shortsnews.user.domain.usecase.user

import com.ns.shortsnews.user.data.models.OTPResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserOtpValidationDataUseCases(private val userDataRepository: UserDataRepository): UseCase<OTPResult, Any>() {
    override suspend fun run(params: Any?, action: String?): OTPResult {
        return userDataRepository.getValidateOtpData(action.toString())
    }
}