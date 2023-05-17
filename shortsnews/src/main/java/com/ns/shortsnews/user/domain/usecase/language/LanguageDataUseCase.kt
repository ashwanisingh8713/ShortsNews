package com.ns.shortsnews.user.domain.usecase.language

import com.ns.shortsnews.user.domain.models.ChannelsDataResult
import com.ns.shortsnews.user.domain.models.LanguagesResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class LanguageDataUseCase(private val userDataRepository: UserDataRepository): UseCase<LanguagesResult, Any>() {
    override suspend fun run(params: Any?): LanguagesResult {
        return userDataRepository.getLanguagesData()
    }
}