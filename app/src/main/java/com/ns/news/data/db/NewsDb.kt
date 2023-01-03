package com.ns.news.data.db

import android.content.Context
import androidx.room.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Database(
    entities = [Section::class, Cell::class, SectionPageRemote::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class NewsDb : RoomDatabase() {
    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        private val roomConverter = DataConverter(moshi)

        fun create(context: Context): NewsDb {
            val databaseBuilder =
                Room.databaseBuilder(context, NewsDb::class.java, "news.db").addTypeConverter(roomConverter)

            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun cellItems(): CellDao
    abstract fun remotePage(): SectionPageRemoteDao
    abstract fun sectionDao(): SectionDao
}


