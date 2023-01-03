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

import android.content.Context
import androidx.room.*
import com.android.example.paging.pagingwithnetwork.reddit.db.SubredditRemoteKeyDao
import com.ns.libpagingwithnetwork.reddit.article.AWData
import com.ns.libpagingwithnetwork.reddit.article.Cell
import com.ns.libpagingwithnetwork.reddit.vo.SubredditRemoteKey
import com.ns.libpagingwithnetwork.reddit.article.CellsItem
import com.ns.libpagingwithnetwork.reddit.article.SectionPageRemote
import com.ns.libpagingwithnetwork.reddit.vo.RedditPost
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Database schema used by the DbRedditPostRepository
 */
@Database(
    entities = [RedditPost::class, SubredditRemoteKey::class, Cell::class, SectionPageRemote::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ArticleConverter::class)
abstract class RedditDb : RoomDatabase() {
    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        private val roomConverter = ArticleConverter(moshi)

        fun create(context: Context, useInMemory: Boolean): RedditDb {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, RedditDb::class.java)
            } else {
                Room.databaseBuilder(context, RedditDb::class.java, "reddit.db").addTypeConverter(roomConverter)
            }
            return databaseBuilder

                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun posts(): RedditPostDao
    abstract fun remoteKeys(): SubredditRemoteKeyDao
    abstract fun cellItems(): CellItemsDao
    abstract fun remotePage(): SectionPageRemoteDao
}


