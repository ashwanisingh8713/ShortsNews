package com.ns.shortsnews.domain.repository

import com.ns.shortsnews.database.LanguageDao
import com.ns.shortsnews.domain.models.LanguageTable
import kotlinx.coroutines.flow.Flow

class LanguageRepository(var languageDao: LanguageDao) {

        suspend fun insert(languageTable: LanguageTable ) {
            languageDao.insert(languageTable)
        }

    suspend fun getAllLanguageData(): Flow<List<LanguageTable>> {
            return languageDao.getLanguageItems()
        }

    suspend fun delete(languageTable: LanguageTable){
            languageDao.delete(languageTable)
        }

    suspend fun update(id: String, isSelected: Boolean){
        languageDao.update(id,isSelected)
    }

    suspend fun isEmpty():Boolean {
     return  languageDao.isEmpty()
    }


}