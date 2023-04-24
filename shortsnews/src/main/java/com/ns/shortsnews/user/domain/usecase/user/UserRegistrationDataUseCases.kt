package com.ns.shortsnews.user.domain.usecase.user

import com.ns.shortsnews.user.data.models.RegistrationResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserRegistrationDataUseCases(private val userDataRepository: UserDataRepository): UseCase<RegistrationResult, Any>() {
    override suspend fun run(params: Any?, action: String?): RegistrationResult {
        return userDataRepository.getUserRegistration(action.toString())
    }
}