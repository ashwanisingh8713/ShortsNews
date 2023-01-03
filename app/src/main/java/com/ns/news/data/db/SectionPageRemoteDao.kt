package com.ns.news.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SectionPageRemoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: SectionPageRemote)

    @Query("SELECT * FROM section_page WHERE sectionId = :sectionId")
    suspend fun remotePageBySection(sectionId: String): SectionPageRemote

    @Query("DELETE FROM section_page WHERE sectionId = :sectionId")
    suspend fun deleteBySection(sectionId: String)
}