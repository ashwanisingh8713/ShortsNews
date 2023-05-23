package com.ns.shortsnews.user.domain.usecase.bookmark

import com.ns.shortsnews.user.domain.models.VideoDataResponse
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserProfileBookmarksUseCase(private val userDataRepository: UserDataRepository): UseCase<VideoDataResponse, Any>() {
    override suspend fun run(params: Any?): VideoDataResponse {
        return userDataRepository.getBookmarksData()
    }
}