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

package com.ns.news.data.db

import android.content.Context
import androidx.room.*
import com.ns.news.domain.model.Cell
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Database(
    entities = [Cell::class, SectionPageRemote::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ArticleConverter::class)
abstract class NewsDb : RoomDatabase() {
    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        private val roomConverter = ArticleConverter(moshi)

        fun create(context: Context): NewsDb {
            val databaseBuilder =
                Room.databaseBuilder(context, NewsDb::class.java, "news.db").addTypeConverter(roomConverter)

            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun cellItems(): CellItemsDao
    abstract fun remotePage(): SectionPageRemoteDao
}


