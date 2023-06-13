package com.ns.shortsnews.domain.usecase.channel

import com.ns.shortsnews.domain.models.ChannelInfo
import com.ns.shortsnews.domain.models.StatusResult
import com.ns.shortsnews.domain.repository.UserDataRepository
import com.ns.shortsnews.domain.usecase.base.UseCase

class ChannelInfoUseCase(val repository: UserDataRepository) : UseCase<ChannelInfo, String>() {
    override suspend fun run(params: String?): ChannelInfo {
        return repository.getChannelInfo(params!!)
    }
}