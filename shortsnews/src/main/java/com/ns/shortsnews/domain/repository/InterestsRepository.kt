package com.ns.shortsnews.domain.repository

import com.ns.shortsnews.database.InterestsDao
import com.ns.shortsnews.domain.models.InterestsTable
import kotlinx.coroutines.flow.Flow

class InterestsRepository(private val categoryDao: InterestsDao) {

    suspend fun insert(interestsTable: InterestsTable) {
        categoryDao.insert(interestsTable)
    }

    suspend fun getAllInterestsData(): Flow<List<InterestsTable>> {
        return categoryDao.getInterestedItems()
    }

    suspend fun delete(interestsTable: InterestsTable){
        categoryDao.delete(interestsTable)
    }

    suspend fun update(id: String, interestsTable: InterestsTable){
        TODO()
    }
}