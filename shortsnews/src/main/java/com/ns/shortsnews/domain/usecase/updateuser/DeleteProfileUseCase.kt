package com.ns.shortsnews.domain.usecase.updateuser

import com.ns.shortsnews.domain.models.StatusResult
import com.ns.shortsnews.domain.repository.UserDataRepository
import com.ns.shortsnews.domain.usecase.base.UseCase

class DeleteProfileUseCase(val repository: UserDataRepository): UseCase<StatusResult, Any>() {
    override suspend fun run(params: Any?): StatusResult {
        return repository.getDeleteProfile()
    }
}