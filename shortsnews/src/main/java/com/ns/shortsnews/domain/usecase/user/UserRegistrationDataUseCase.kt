package com.ns.shortsnews.domain.usecase.user

import com.ns.shortsnews.domain.models.RegistrationResult
import com.ns.shortsnews.domain.usecase.base.UseCase
import com.ns.shortsnews.domain.repository.UserDataRepository

class UserRegistrationDataUseCase(private val userDataRepository: UserDataRepository): UseCase<RegistrationResult, Any>() {
    override suspend fun run(params: Any?): RegistrationResult {
        return userDataRepository.getUserRegistration(params as Map<String, String>)
    }
}