package com.ns.shortsnews.domain.usecase.followunfollow

import com.ns.shortsnews.domain.repository.UserDataRepository
import com.ns.shortsnews.domain.usecase.base.UseCase
import com.videopager.data.Following

class FollowUnfollowUseCase(val repository: UserDataRepository):UseCase<Following, String?>() {
    override suspend fun run(params: String?): Following {
        return repository.getFollowUnfollow(params!!)
    }
}