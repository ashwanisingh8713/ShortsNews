package com.ns.shortsnews.domain.usecase.video_category

import com.google.gson.JsonObject
import com.ns.shortsnews.domain.models.UpdateCategories
import com.ns.shortsnews.domain.repository.VideoCategoryRepository
import com.ns.shortsnews.domain.usecase.base.UseCase

class UpdateVideoCategoriesUseCase(private val videoCategoryRepository: VideoCategoryRepository): UseCase<UpdateCategories, Any>() {
    override suspend fun run(params: Any?): UpdateCategories {
        return videoCategoryRepository.getUpdateCategories(params!!)
    }
}