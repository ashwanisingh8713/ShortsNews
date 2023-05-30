package com.ns.shortsnews.domain.usecase.user

import com.ns.shortsnews.domain.models.ProfileResult
import com.ns.shortsnews.domain.usecase.base.UseCase
import com.ns.shortsnews.domain.repository.UserDataRepository

class UserProfileDataUseCase(private val userDataRepository: UserDataRepository): UseCase<ProfileResult, Any>() {
    override suspend fun run(params: Any?): ProfileResult {
        return userDataRepository.getUserProfileData()
    }
}