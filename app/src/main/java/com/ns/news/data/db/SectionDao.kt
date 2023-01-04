package com.ns.news.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cells: List<Section>)

    @Query("SELECT * FROM Section WHERE inBreadcrumb = :inBreadcrumb")
    fun getBreadcrumb(inBreadcrumb: Boolean = true): Flow<List<Section>>

    @Query("SELECT * FROM Section WHERE inHamburger = :inHamburger")
    fun getHamburger(inHamburger: Boolean = true): Flow<List<Section>>

    @Query("DELETE FROM Section WHERE sectionId = :sectionId")
    suspend fun deleteBySectionId(sectionId: String)

    @Query("DELETE FROM Section")
    suspend fun deleteAll()

}
