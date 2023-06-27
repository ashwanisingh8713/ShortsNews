package com.ns.shortsnews.domain.usecase.notification

import com.ns.shortsnews.domain.models.StatusResult
import com.ns.shortsnews.domain.repository.UserDataRepository
import com.ns.shortsnews.domain.usecase.base.UseCase
import okhttp3.RequestBody

class FCMTokenDataUseCase(private val userDataRepository: UserDataRepository): UseCase<StatusResult, Any>()  {
    override suspend fun run(params: Any?): StatusResult {
        return userDataRepository.getNotificationTokenStatus(params as Map<String, String>)
    }
}