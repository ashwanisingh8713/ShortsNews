package com.ns.shortsnews.user.domain.usecase

import com.ns.shortsnews.user.data.models.BaseResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.squareup.moshi.Json

class UserUseCases(private val userDataRepository: UserDataRepository): UseCase<BaseResult, Any>() {
    override suspend fun run(params: Any?, action: String?): BaseResult {
        TODO("Not yet implemented")
    }
}