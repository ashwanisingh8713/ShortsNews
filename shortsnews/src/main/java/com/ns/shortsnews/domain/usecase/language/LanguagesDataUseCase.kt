package com.ns.shortsnews.domain.usecase.language

import com.ns.shortsnews.domain.models.LanguagesResult
import com.ns.shortsnews.domain.repository.UserDataRepository
import com.ns.shortsnews.domain.usecase.base.UseCase

class LanguagesDataUseCase(val repository: UserDataRepository):
    UseCase<LanguagesResult, Any>() {
    override suspend fun run(params: Any?): LanguagesResult {
        return repository.getLanguagesData()
    }
}