package com.ns.news.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.api.model.CellBackground
import com.ns.news.domain.model.ViewType


@Entity(tableName = "Bookmark")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val actionText: String,
    val data: List<AWDataItem>,
    val link: String,
    val cellType: String,
    val type: String,
    val title: String,
    val sectionId: String,
    val viewType: ViewType,
    val cellBg: CellBackground
)

/*@Entity(tableName = "Bookmark")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
): AWDataItem()*/
