package com.ns.shortsnews.user.domain.usecase.user

import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserOtpValidationDataUseCase(private val userDataRepository: UserDataRepository): UseCase<OTPResult, Any>() {
    override suspend fun run(params: Any?): OTPResult {
        return userDataRepository.getValidateOtpData(params as String)
    }
}