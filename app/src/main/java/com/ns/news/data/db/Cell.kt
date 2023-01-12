package com.ns.news.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.api.model.CellBackground
import com.ns.news.domain.model.ViewType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Cell", indices = [Index(value = ["sectionId"], unique = false)])
data class Cell(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val actionText: String,
    @IgnoredOnParcel
    val data: List<AWDataItem>,
    val link: String,
    val cellType: String,
    val type: String,
    val title: String,
    val sectionId: String,
    val viewType: ViewType,
    val cellBg: CellBackground
): Parcelable