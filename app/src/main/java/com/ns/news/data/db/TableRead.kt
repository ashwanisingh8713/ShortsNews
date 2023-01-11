package com.ns.news.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TableRead")
data class TableRead(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val articleId:String
    )