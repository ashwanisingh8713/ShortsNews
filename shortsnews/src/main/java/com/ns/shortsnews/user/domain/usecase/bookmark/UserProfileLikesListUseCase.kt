package com.ns.shortsnews.user.domain.usecase.bookmark

import com.ns.shortsnews.user.domain.models.LikesResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserProfileLikesListUseCase(private val userDataRepository: UserDataRepository): UseCase<LikesResult, Any>() {
    override suspend fun run(params: Any?): LikesResult {
        return userDataRepository.getLikesData()
    }
}