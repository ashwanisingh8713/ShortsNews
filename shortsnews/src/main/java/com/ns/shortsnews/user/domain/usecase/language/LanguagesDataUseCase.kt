package com.ns.shortsnews.user.domain.usecase.language

import com.ns.shortsnews.user.domain.models.LanguagesResult
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.ns.shortsnews.user.domain.usecase.base.UseCase

class LanguagesDataUseCase(val repository: UserDataRepository):
    UseCase<LanguagesResult, Any>() {
    override suspend fun run(params: Any?): LanguagesResult {
        return repository.getLanguagesData()
    }
}