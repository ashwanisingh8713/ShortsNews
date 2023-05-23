package com.ns.shortsnews.user.domain.usecase.bookmark

import com.ns.shortsnews.user.domain.models.BookmarksResult
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository

class UserProfileBookmarksUseCase(private val userDataRepository: UserDataRepository): UseCase<BookmarksResult, Any>() {
    override suspend fun run(params: Any?): BookmarksResult {
        return userDataRepository.getBookmarksData()
    }
}