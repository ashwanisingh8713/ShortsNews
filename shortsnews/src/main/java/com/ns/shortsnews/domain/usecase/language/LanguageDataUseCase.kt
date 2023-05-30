package com.ns.shortsnews.domain.usecase.language

import com.ns.shortsnews.domain.models.LanguagesResult
import com.ns.shortsnews.domain.usecase.base.UseCase
import com.ns.shortsnews.domain.repository.UserDataRepository

class LanguageDataUseCase(private val userDataRepository: UserDataRepository): UseCase<LanguagesResult, Any>() {
    override suspend fun run(params: Any?): LanguagesResult {
        return userDataRepository.getLanguagesData()
    }
}