package com.ns.shortsnews.user.domain.repository

import com.ns.shortsnews.user.domain.models.VideoCategoryResult

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
interface VideoCategoryRepository {
    suspend fun getVideoCategory(): VideoCategoryResult
}