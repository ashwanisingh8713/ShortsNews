/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ns.pagingwithnetwork.reddit.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ns.libpagingwithnetwork.reddit.article.Cell
import com.ns.libpagingwithnetwork.reddit.article.CellsItem

@Dao
interface CellItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cells: List<Cell>)

    @Query("SELECT * FROM Cell WHERE sectionId = :sectionId")
    fun articleBySectionId(sectionId: String): PagingSource<Int, Cell>

    @Query("DELETE FROM Cell WHERE sectionId = :sectionId")
    suspend fun deleteBySectionId(sectionId: String)

//    @Query("SELECT MAX(indexInResponse) + 1 FROM CellsItem WHERE sectionId = :sectionId")
//    suspend fun getNextIndexInSubreddit(sectionId: String): Int
}
