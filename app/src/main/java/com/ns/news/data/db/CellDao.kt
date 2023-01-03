package com.ns.news.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cells: List<Cell>)

    @Query("SELECT * FROM Cell WHERE sectionId = :sectionId")
    fun articleBySectionId(sectionId: String): PagingSource<Int, Cell>

    @Query("DELETE FROM Cell WHERE sectionId = :sectionId")
    suspend fun deleteBySectionId(sectionId: String)

//    @Query("SELECT MAX(indexInResponse) + 1 FROM CellsItem WHERE sectionId = :sectionId")
//    suspend fun getNextIndexInSubreddit(sectionId: String): Int
}
