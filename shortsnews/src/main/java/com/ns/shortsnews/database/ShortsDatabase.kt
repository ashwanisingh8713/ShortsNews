package com.ns.shortsnews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ns.shortsnews.domain.models.InterestsTable
import com.ns.shortsnews.domain.models.LanguageTable


@Database(entities = [LanguageTable::class, InterestsTable::class], version = 2, exportSchema = false)
 abstract class ShortsDatabase : RoomDatabase(){

    abstract fun languageDao():LanguageDao

    abstract fun interestsDao():InterestsDao

    companion object {
        @Volatile
        var instance:ShortsDatabase?=null
        private const val DATABASE_NAME="shorts_database"

        fun getInstance(context: Context):ShortsDatabase?
        {
            if(instance == null)
            {
                synchronized(ShortsDatabase::class.java)
                {
                    if(instance == null)
                    {
                        instance= Room.databaseBuilder(context,ShortsDatabase::class.java,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }

            return instance
        }

    }
}