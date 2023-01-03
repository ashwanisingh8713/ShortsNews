package com.ns.news.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "section_page")
data class SectionPageRemote(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val sectionId: String, // technically mutable but fine for a demo
    val nextPageKey: Int = 1
)
