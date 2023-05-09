package com.ns.shortsnews.user.domain.usecase.channel

import com.ns.shortsnews.user.domain.models.ChannelVideoDataResult
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.ns.shortsnews.user.domain.usecase.base.UseCase

class ChannelVideosDataUseCase(val repository: UserDataRepository):
    UseCase<ChannelVideoDataResult, Any>() {
    override suspend fun run(params: Any?): ChannelVideoDataResult {
        return repository.geChannelVideoData(params.toString())
    }
}