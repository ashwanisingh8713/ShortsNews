package com.ns.shortsnews.database

import androidx.room.*
import com.ns.shortsnews.domain.models.LanguageTable
import kotlinx.coroutines.flow.Flow


@Dao
interface LanguageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(languageData: LanguageTable)

    @Query("SELECT * FROM language ORDER BY id")
    fun getLanguageItems(): Flow<List<LanguageTable>>

    @Delete
    suspend fun delete(languageData: LanguageTable)

    @Query("UPDATE language SET selected = :selected WHERE id = :id ")
    suspend fun update(id:String, selected: Boolean)

    @Query("SELECT (SELECT COUNT(*) FROM language) == 0")
    suspend fun isEmpty():Boolean


    @Query("DELETE FROM language")
    suspend fun deleteLanguageData()
}