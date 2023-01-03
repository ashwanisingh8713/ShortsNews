package com.ns.news.data.api.model


import com.ns.news.domain.model.Cell
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CellsItem(@Json(name = "action_text")
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
                     val title: String = "") {

    companion object {
        fun of(
               actionText: String,
               data: List<AWDataItem>,
               link: String,
               sectionId: String,
               cellType: String,
               type: String,
               title: String):Cell {
            return Cell(actionText= actionText, data = data, link = link,sectionId = sectionId,
                cellType = cellType, type = type, title = title)
        }
    }
}

@JsonClass(generateAdapter = true)
data class AWDataItem(@Json(name = "category_name")
                    val categoryName: String = "",
                      @Json(name = "modified_gmt")
                    val modifiedGmt: String = "",
                      @Json(name = "author")
                    val author: List<AuthorItem> = emptyList(),
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
                    val id: Int = 0,
                      @Json(name = "excerpt")
                    val excerpt: String = "")

@JsonClass(generateAdapter = true)
data class MediaItem(@Json(name = "images")
                     val images: Images,
                     @Json(name = "caption")
                     val caption: String = "")

@JsonClass(generateAdapter = true)
data class Images(@Json(name = "thumbnail")
                  val thumbnail: String = "",
                  @Json(name = "large")
                  val large: String = "",
                  @Json(name = "medium")
                  val medium: String = "",
                  @Json(name = "medium_large")
                  val mediumLarge: String = "")

@JsonClass(generateAdapter = true)
data class ArticleNdWidgetResponse(@Json(name = "msg")
                           val msg: String = "",
                                   @Json(name = "data")
                           val data: AWData = AWData(),
                                   @Json(name = "status")
                           val status: Boolean = false)

@JsonClass(generateAdapter = true)
data class AWData(@Json(name = "total")
                val total: Int = 0,
                  @Json(name = "cells")
                val cells: List<CellsItem> = emptyList(),
                  @Json(name = "page")
                val page: Int = 0)

@JsonClass(generateAdapter = true)
data class AuthorItem(@Json(name = "avatar_url")
                      val avatarUrl: String = "",
                      @Json(name = "name")
                      val name: String = "")


