package com.ns.news.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ns.news.data.api.model.Logo
import com.ns.news.data.api.model.SectionItem
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Section")
@Parcelize
data class Section(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val sectionId: String,
    val api: String,
    val name: String,
    val inHamburger: Boolean,
    val inBreadcrumb: Boolean,
    @IgnoredOnParcel
    val logo: Logo,
    @IgnoredOnParcel
    val subSections: List<SectionItem>,
    val slug: String
) : Parcelable