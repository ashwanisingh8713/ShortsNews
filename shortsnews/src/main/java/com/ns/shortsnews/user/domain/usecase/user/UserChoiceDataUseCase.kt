package com.ns.shortsnews.user.domain.usecase.user

import com.ns.shortsnews.user.domain.models.UserChoiceResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserChoiceDataUseCase(private val userDataRepository: UserDataRepository): UseCase<UserChoiceResult, Any>() {
    override suspend fun run(params: Any?): UserChoiceResult {
        return userDataRepository.getUserChoiceData()
    }
}