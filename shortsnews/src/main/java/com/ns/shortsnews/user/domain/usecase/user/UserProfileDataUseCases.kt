package com.ns.shortsnews.user.domain.usecase.user

import com.ns.shortsnews.user.data.models.ProfileResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserProfileDataUseCases(private val userDataRepository: UserDataRepository): UseCase<ProfileResult, Any>() {
    override suspend fun run(params: Any?, action: String?): ProfileResult {
        return userDataRepository.getProfileData()
    }
}