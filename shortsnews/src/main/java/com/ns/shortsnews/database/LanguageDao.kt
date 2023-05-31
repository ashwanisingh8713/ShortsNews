package com.ns.shortsnews.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ns.shortsnews.domain.models.LanguageTable
import kotlinx.coroutines.flow.Flow


@Dao
interface LanguageDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(languageData: LanguageTable)

    @Query("SELECT * FROM language ORDER BY id")
    fun getLanguageItems(): Flow<List<LanguageTable>>

    @Delete
    suspend fun delete(languageData: LanguageTable)

    @Query("UPDATE language SET selected = :selected WHERE id = :id ")
    suspend fun update(id:String, selected: Boolean)
}