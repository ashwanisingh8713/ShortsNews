package com.ns.shortsnews.user.domain.usecase.video_category

import com.ns.shortsnews.user.domain.models.VideoCategoryResult
import com.ns.shortsnews.user.domain.repository.VideoCategoryRepository
import com.ns.shortsnews.user.domain.usecase.base.UseCase

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryUseCase(private val videoCategoryRepository: VideoCategoryRepository):UseCase<VideoCategoryResult, Any>() {
    override suspend fun run(params: Any?): VideoCategoryResult {
        return videoCategoryRepository.getVideoCategory()
    }
}