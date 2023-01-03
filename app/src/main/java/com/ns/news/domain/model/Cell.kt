package com.ns.news.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ns.news.data.api.model.AWDataItem

@Entity(tableName = "Cell", indices = [Index(value = ["sectionId"], unique = false)])
data class Cell(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val actionText: String,
    val data: List<AWDataItem>,
    val link: String,
    val cellType: String,
    val type: String,
    val title: String,
    val sectionId: String,
)