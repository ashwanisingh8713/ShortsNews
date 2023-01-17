package com.ns.news.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.api.model.CellBackground
import com.ns.news.domain.model.ViewType


@Entity(tableName = "Bookmark")
data class Bookmark(
    @PrimaryKey
    val articleId:String,
    val data: AWDataItem,
    val type: String
)

/*@Entity(tableName = "Bookmark")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
): AWDataItem()*/
