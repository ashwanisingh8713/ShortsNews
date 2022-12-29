package com.news.data.api.model


import com.ns.news.data.api.model.Logo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(@Json(name = "default_selection")
                          var defaultSelection: Boolean = false,
                    @Json(name = "name")
                          val name: String = "",
                    @Json(name = "logo")
                          val logo: Logo,
                    @Json(name = "categoryId")
                          val categoryId: Int = 0,
                    @Json(name = "slug")
                          val slug: String = "",
                    var is_selected : Boolean = false)

@JsonClass(generateAdapter = true)
data class CategoryPageData(@Json(name = "minimum_selection")
                val minimumSelection: Int = 0,
                            @Json(name = "category_page_logo")
                val categoryPageLogo: Logo = Logo(),
                            @Json(name = "category_page_title")
                val categoryPageTitle: String = "",
                            @Json(name = "categories")
                val categories: List<Category>? = emptyList(),
                            @Json(name = "category_page_sub_title")
                val categoryPageSubTitle: String = "")

@JsonClass(generateAdapter = true)
data class CategoryResponse(@Json(name = "msg")
                  val msg: String = "",
                            @Json(name = "data")
                  val data: CategoryPageData,
                            @Json(name = "status")
                  val status: Boolean = false)





