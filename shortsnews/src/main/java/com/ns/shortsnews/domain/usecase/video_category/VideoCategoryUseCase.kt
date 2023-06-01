package com.ns.shortsnews.domain.usecase.video_category

import com.ns.shortsnews.domain.models.VideoCategoryResult
import com.ns.shortsnews.domain.repository.VideoCategoryRepository
import com.ns.shortsnews.domain.usecase.base.UseCase

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
class VideoCategoryUseCase(private val videoCategoryRepository: VideoCategoryRepository):
    UseCase<VideoCategoryResult, String>() {
    override suspend fun run(params: String?): VideoCategoryResult {
        return videoCategoryRepository.getVideoCategory(params!!)
    }
}