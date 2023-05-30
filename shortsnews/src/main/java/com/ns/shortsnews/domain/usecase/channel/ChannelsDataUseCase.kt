package com.ns.shortsnews.domain.usecase.channel

import com.ns.shortsnews.domain.models.ChannelsDataResult
import com.ns.shortsnews.domain.models.OTPResult
import com.ns.shortsnews.domain.usecase.base.UseCase
import com.ns.shortsnews.domain.repository.UserDataRepository

class ChannelsDataUseCase(private val userDataRepository: UserDataRepository): UseCase<ChannelsDataResult, Any>() {
    override suspend fun run(params: Any?): ChannelsDataResult {
        return userDataRepository.getChannelsData()
    }
}