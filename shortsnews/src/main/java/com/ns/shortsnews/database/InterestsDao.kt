package com.ns.shortsnews.database

import androidx.room.*
import com.ns.shortsnews.domain.models.InterestsTable
import kotlinx.coroutines.flow.Flow
@Dao
interface InterestsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(interestsTable: InterestsTable)

    @Query("SELECT * FROM interests ORDER BY id")
    fun getInterestedItems(): Flow<List<InterestsTable>>

    @Delete
    suspend fun delete(interestsTable: InterestsTable)

    @Update
    suspend fun update(interestsTable: InterestsTable)
}