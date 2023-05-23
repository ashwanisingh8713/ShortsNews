package com.ns.shortsnews.user.domain.usecase.bookmark

import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl

class UserProfileBookmarksUseCase(private val userDataRepository: UserDataRepository): UseCase<VideoDataRepositoryImpl.VideoDataResponse, Any>() {
    override suspend fun run(params: Any?): VideoDataRepositoryImpl.VideoDataResponse {
        return userDataRepository.getBookmarksData()
    }
}