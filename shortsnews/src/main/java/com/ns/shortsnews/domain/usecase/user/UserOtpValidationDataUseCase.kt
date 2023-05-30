package com.ns.shortsnews.domain.usecase.user

import com.ns.shortsnews.domain.models.OTPResult
import com.ns.shortsnews.domain.usecase.base.UseCase
import com.ns.shortsnews.domain.repository.UserDataRepository

class UserOtpValidationDataUseCase(private val userDataRepository: UserDataRepository): UseCase<OTPResult, Any>() {
    override suspend fun run(params: Any?): OTPResult {
        return userDataRepository.getValidateOtpData(params as Map<String, String>)
    }
}