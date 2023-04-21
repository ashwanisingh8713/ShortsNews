package com.ns.shortsnews.user.domain.usecase

import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.squareup.moshi.Json

class UserUseCases(private val userDataRepository: UserDataRepository): UseCase<Json, Any>() {
    override suspend fun run(params: Any?, action: String?): Json {
        TODO("Not yet implemented")
    }
}