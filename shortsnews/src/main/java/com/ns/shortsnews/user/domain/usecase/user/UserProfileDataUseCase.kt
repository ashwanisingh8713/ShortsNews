package com.ns.shortsnews.user.domain.usecase.user

import com.ns.shortsnews.user.domain.models.ProfileResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserProfileDataUseCase(private val userDataRepository: UserDataRepository): UseCase<ProfileResult, Any>() {
    override suspend fun run(params: Any?): ProfileResult {
        return userDataRepository.getUserProfileData()
    }
}