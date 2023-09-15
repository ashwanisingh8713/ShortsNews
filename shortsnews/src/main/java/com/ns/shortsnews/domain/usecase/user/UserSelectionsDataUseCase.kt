package com.ns.shortsnews.domain.usecase.user

import com.ns.shortsnews.domain.models.UserSelectionResult
import com.ns.shortsnews.domain.repository.UserDataRepository
import com.ns.shortsnews.domain.usecase.base.UseCase

class UserSelectionsDataUseCase(private val repository: UserDataRepository) : UseCase<UserSelectionResult, Any>(){
    override suspend fun run(params: Any?): UserSelectionResult {
        return repository.getUserSelections()
    }
}