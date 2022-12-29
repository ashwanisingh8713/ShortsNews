package com.ns.news.domain.model

import android.os.Parcelable
import com.ns.news.data.api.model.CellsItem
import kotlinx.android.parcel.Parcelize

data class ArticleNdWidgetDataState(
    val loading: Boolean = true,
    val cellItems: List<CellsItem> = emptyList(),
    val page: Int = 0
    )

@Parcelize
data class Section (val sectionId: Int, val sectionName: String, val sectionApi: String,
                    val hasSubsection: Boolean = false): Parcelable

