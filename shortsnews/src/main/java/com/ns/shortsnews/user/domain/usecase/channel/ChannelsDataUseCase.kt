package com.ns.shortsnews.user.domain.usecase.channel

import com.ns.shortsnews.user.domain.models.ChannelsDataResult
import com.ns.shortsnews.user.domain.models.OTPResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class ChannelsDataUseCase(private val userDataRepository: UserDataRepository): UseCase<ChannelsDataResult, Any>() {
    override suspend fun run(params: Any?): ChannelsDataResult {
        return userDataRepository.getChannelsData()
    }
}