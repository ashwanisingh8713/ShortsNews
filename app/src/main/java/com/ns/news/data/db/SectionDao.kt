package com.ns.news.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cells: List<Section>)

    @Query("SELECT * FROM Section")
    fun getSections(): PagingSource<Int, Section>

    @Query("DELETE FROM Cell WHERE sectionId = :sectionId")
    suspend fun deleteBySectionId(sectionId: String)

    @Query("DELETE FROM Section")
    fun deleteAll()

}
