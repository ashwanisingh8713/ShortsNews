package com.ns.news.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ns.news.data.api.model.Logo
import com.ns.news.data.api.model.SectionItem

@Entity(tableName = "Section")
data class Section(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val sectionId: String,
    val api: String,
    val name: String,
    val inHamburger: Boolean,
    val inBreadcrumb: Boolean,
    val logo: Logo,
    val subSections: List<SectionItem>,
    val slug: String
)