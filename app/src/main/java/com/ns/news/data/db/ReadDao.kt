package com.ns.news.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReadArticle(tableRead: TableRead?)

    @Query("SELECT * FROM TableRead")
    fun getAllReadArticles(): Flow<List<TableRead?>>

    @Query("SELECT * FROM TableRead WHERE articleId = :articleId")
    fun getArticle(articleId: String): Flow<TableRead?>
}