package com.ns.news.data.api.model


import android.os.Parcelable
import com.ns.news.data.api.model.CellsItem.Companion.defaultColorDay
import com.ns.news.data.api.model.CellsItem.Companion.defaultColorNight
import com.ns.news.data.db.Cell
import com.ns.news.domain.model.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class CellsItem(
    @Json(name = "action_text")
    val actionText: String = "",
    @Json(name = "data")
    val data: List<AWDataItem> = emptyList(),
    @Json(name = "link")
    val link: String = "",
    @Json(name = "section_id")
    val sectionId: String = "",
    @Json(name = "cell_type")
    val cellType: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "title")
    val title: String = ""
) {
    companion object {
        const val defaultColorDay = "#808080"
        const val defaultColorNight = "#FAFFF7"
        const val CELLTYPE_WIDGET = "WIDGET"
        const val CELLTYPE_ARTICLE = "ARTICLE"
        fun of(
            actionText: String,
            data: List<AWDataItem>,
            link: String,
            sectionId: String,
            cellType: String,
            type: String,
            title: String
        ): Cell {
            var colorDay = defaultColorDay
            var colorNight = defaultColorNight
            var cellType = cellType

            var viewType = when (type) {
                "PHOTOS" -> {
                    colorDay = "#ffffff"
                    colorNight = "#d69aed"
                    ViewType.ARTICLE_VT_PHOTOS
                }
                "VIDEO" -> {
                    colorDay = "#eda279"
                    colorNight = "#e36e2d"
                    ViewType.ARTICLE_VT_VIDEO
                }
                "STANDARD" -> {
                    colorDay = "#ffffff"
                    colorNight = "#a19e97"
                    ViewType.ARTICLE_VT_STANDARD
                }
                "TOP_NEWS_COROUSAL" -> {
                    colorDay = "#f6f6f6"
                    colorNight = "#665f39"
                    ViewType.WIDGET_VT_TOP_NEWS_COROUSAL
                }
                "HERO_PLAIN_WIDGET" -> {
                    colorDay = "#ffffff"
                    colorNight = "#aba791"
                    cellType = CELLTYPE_ARTICLE
                    ViewType.WIDGET_VT_HERO_PLAIN_WIDGET
                }
                "WEB_WIDGET" -> {
                    colorDay = "#969692"
                    colorNight = "#54544c"
                    ViewType.WIDGET_VT_WEB_WIDGET
                }
                "PLAIN_WITH_COROUSAL" -> {
                    colorDay = "#6e6e46"
                    colorNight = "#b0b086"
                    ViewType.WIDGET_VT_PLAIN_WITH_COROUSAL
                }
                "STACK_CARD_WITH_COROUSAL" -> {
                    colorDay = "#f6f6f6"
                    colorNight = "#4b4778"
                    ViewType.WIDGET_VT_STACK_CARD_WITH_COROUSAL
                }
                "ALL_TOPICS_WIDGET" -> {
                    colorDay = "#476878"
                    colorNight = "#718791"
                    ViewType.WIDGET_VT_ALL_TOPICS_WIDGET
                }
                "FOR_YOU_WIDGET" -> {
                    colorDay = "#bfa7d1"
                    colorNight = "#8b7999"
                    cellType = CELLTYPE_ARTICLE
                    ViewType.WIDGET_VT_FOR_YOU_WIDGET
                }
                "GLAMOR_COROUSAL_WIDGET" -> {
                    colorDay = "#ffffff"
                    colorNight = "#a64c83"
                    ViewType.WIDGET_VT_GLAMOR_COROUSAL_WIDGET
                }
                "PAGER_GALLERY" -> {
                    colorDay = "#e4e4e4"
                    colorNight = "#a61aab"
                    ViewType.WIDGET_VT_PAGER_GALLERY
                }
                else ->{
                    ViewType.VT_NOT_DEFINED
                }
            }

            return Cell(
                actionText = actionText, data = data, link = link, sectionId = sectionId,
                cellType = cellType, type = type, title = title, viewType = viewType,
                cellBg = CellBackground(colorDay, colorNight)
            )
        }
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
open class AWDataItem(
    @Json(name = "category_name")
    val categoryName: String = "",
    @Json(name = "modified_gmt")
    val modifiedGmt: String = "",
    @Json(name = "author")
    val author: List<AuthorItem> = emptyList(),
    @Json(name = "highlights")
    val highlights: List<String> = emptyList(),
    @Json(name = "tags")
    val tags: List<TagItem> = emptyList(),
    @Json(name = "link")
    val link: String = "",
    @Json(name = "format")
    val format: String = "",
    @Json(name = "media")
    val media: List<MediaItem> = emptyList(),
    @Json(name = "title")
    val title: String = "",
    @Json(name = "cell_annotation")
    val cellAnnotation: String = "",
    @Json(name = "content")
    val content: String = "",
    @Json(name = "action_text")
    val actionText: String = "",
    @Json(name = "is_premium")
    val isPremium: Boolean = false,
    @Json(name = "category_id")
    val categoryId: String = "",
    @Json(name = "action_value")
    val actionValue: String = "",
    @Json(name = "id")
    val articleId: String = "",
    @Json(name = "excerpt")
    val excerpt: String = ""
): Parcelable {

    override fun equals(other: Any?): Boolean{
        if(other is AWDataItem){
            return articleId == (other.articleId)
        }
        return false
    }

    override fun hashCode(): Int {
        return articleId.hashCode()
    }
}


@Parcelize
@JsonClass(generateAdapter = true)
data class MediaItem(
    @Json(name = "images")
    val images: Images,
    @Json(name = "caption")
    val caption: String = ""
):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Images(
    @Json(name = "thumbnail")
    val thumbnail: String = "",
    @Json(name = "large")
    val large: String = "",
    @Json(name = "medium")
    val medium: String = "",
    @Json(name = "medium_large")
    val mediumLarge: String = ""
):Parcelable

@JsonClass(generateAdapter = true)
data class ArticleNdWidgetResponse(
    @Json(name = "msg")
    val msg: String = "",
    @Json(name = "data")
    val data: AWData = AWData(),
    @Json(name = "status")
    val status: Boolean = false
)

@JsonClass(generateAdapter = true)
data class AWData(
    @Json(name = "total")
    val total: Int = 0,
    @Json(name = "cells")
    val cells: List<CellsItem> = emptyList(),
    @Json(name = "page")
    val page: Int = 0
)

@Parcelize
@JsonClass(generateAdapter = true)
data class AuthorItem(
    @Json(name = "avatar_url")
    val avatarUrl: String = "",
    @Json(name = "name")
    val name: String = ""
):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TagItem(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = ""
):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CellBackground(
    val colorDay: String = defaultColorDay,
    val colorNight: String = defaultColorNight
):Parcelable {

}



