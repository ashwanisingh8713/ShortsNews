package com.ns.news.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cells: Bookmark)

    @Query("SELECT * FROM Bookmark WHERE sectionId = :articleId")
    fun getAllBookmarks(articleId: String): Flow<List<Bookmark>>

    @Query("SELECT * FROM Bookmark WHERE sectionId = :articleId")
    fun bookmarkArticle(articleId: String): Flow<Bookmark?>

    @Query("DELETE FROM Bookmark WHERE sectionId = :articleId")
    suspend fun deleteByArticleId(articleId: String)

}
