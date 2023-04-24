package com.ns.shortsnews.user.data.mapper

import com.ns.shortsnews.user.domain.models.VideoCategory

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
data class UserVideoCategory(
    val status: Boolean = false,
    val videoCategories: List<VideoCategory> = emptyList()
) {
    fun mapper(status: Boolean, videoCategories: List<VideoCategory>):UserVideoCategory {
        return UserVideoCategory(status = status, videoCategories = videoCategories)
    }
}
