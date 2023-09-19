package com.ns.shortsnews.data.repository

import com.google.gson.JsonObject
import com.ns.shortsnews.data.source.UserApiService
import com.ns.shortsnews.domain.models.UpdateCategories
import com.ns.shortsnews.domain.models.VideoCategoryResult
import com.ns.shortsnews.domain.repository.VideoCategoryRepository

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryRepositoryImp(private val apiService: UserApiService): VideoCategoryRepository {
    override suspend fun getVideoCategory(languages:String): VideoCategoryResult {
        return apiService.getVideoCategory(languages)
    }

    override suspend fun getUpdateCategories(categories:Any): UpdateCategories {
        return apiService.updateCategory(categories)
    }
}