package com.ns.shortsnews.database

import androidx.room.*
import com.ns.shortsnews.domain.models.InterestsTable
import kotlinx.coroutines.flow.Flow
@Dao
interface InterestsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(interestsTable: InterestsTable)

    @Query("SELECT * FROM interests ORDER BY id")
    fun getInterestedItems(): Flow<List<InterestsTable>>

    @Delete
    suspend fun delete(interestsTable: InterestsTable)

    @Query("UPDATE interests SET selected = :selected WHERE id = :id ")
    suspend fun update(id:String, selected: Boolean)

    @Query("SELECT (SELECT COUNT(*) FROM interests) == 0")
    suspend fun isEmpty():Boolean

    @Query("DELETE FROM interests")
    suspend fun deleteInterestsData()
}