package com.ns.shortsnews.user.domain.usecase.videodata

import com.ns.shortsnews.user.domain.models.VideoDataResponse
import com.ns.shortsnews.user.domain.usecase.base.UseCase
import com.ns.shortsnews.user.domain.repository.UserDataRepository
import com.videopager.utils.CategoryConstants

class VideoDataUseCase(private val userDataRepository: UserDataRepository): UseCase<VideoDataResponse, Pair<String, String>>() {

    override suspend fun run(params: Pair<String, String>?): VideoDataResponse {
        val response = when(params?.first) {
            CategoryConstants.BOOKMARK_VIDEO_DATA -> userDataRepository.getBookmarksData()
            CategoryConstants.CHANNEL_VIDEO_DATA -> userDataRepository.getChannelVideoData(channelId = params.second)
            else -> VideoDataResponse()
        }
        return response
    }
}