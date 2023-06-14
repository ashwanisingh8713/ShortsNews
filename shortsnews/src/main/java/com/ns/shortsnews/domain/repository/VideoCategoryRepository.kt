package com.ns.shortsnews.domain.repository

import com.ns.shortsnews.domain.models.VideoCategoryResult

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
interface VideoCategoryRepository {
    suspend fun getVideoCategory(languages:String): VideoCategoryResult
}