package com.ns.shortsnews.user.data.repository

import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.user.domain.models.VideoCategoryResult
import com.ns.shortsnews.user.domain.repository.VideoCategoryRepository

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryRepositoryImp(private val apiService: UserApiService): VideoCategoryRepository {
    override suspend fun getVideoCategory(): VideoCategoryResult {
        return apiService.getVideoCategory()
    }
}