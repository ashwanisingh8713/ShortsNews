package com.ns.shortsnews.user.domain.usecase.user

import com.ns.shortsnews.user.data.models.RegistrationResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserRegistrationDataUseCase(private val userDataRepository: UserDataRepository): UseCase<RegistrationResult, Any>() {
    override suspend fun run(params: Any?): RegistrationResult {
        return userDataRepository.getUserRegistration(params.toString())
    }
}