package com.news.data.api.model


import com.ns.news.data.api.model.Logo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class States(@Json(name = "states_page_title")
                  val statesPageTitle: String = "",
                  @Json(name = "states_page_logo")
                  val statesPageLogo: Logo = Logo(),
                  @Json(name = "data")
                  val data: List<DataItem>? = emptyList(),
                  @Json(name = "states_page_sub_title")
                  val statesPageSubTitle: String = "")

@JsonClass(generateAdapter = true)
data class LanguageResponse(@Json(name = "msg")
                            val msg: String = "",
                            @Json(name = "data")
                            val data: LanguagePageData,
                            @Json(name = "status")
                            val status: Boolean = false)

@JsonClass(generateAdapter = true)
data class DataItem(@Json(name = "default_selection")
                    val defaultSelection: Boolean = false,
                    @Json(name = "name")
                    val name: String = "",
                    @Json(name = "logo")
                    val logo: Logo,
                    @Json(name = "state_id")
                    val stateId: Int = 0,
                    @Json(name = "slug")
                    val slug: String = "")



@JsonClass(generateAdapter = true)
data class LanguagePageData(@Json(name = "language_page_sub_title")
                val languagePageSubTitle: String = "",
                            @Json(name = "languages")
                val languages: List<LanguagesItem>? = emptyList(),
                            @Json(name = "language_page_logo")
                val languagePageLogo: Logo = Logo(),
                            @Json(name = "language_page_title")
                val languagePageTitle: String = "")

@JsonClass(generateAdapter = true)
data class LanguagesItem(@Json(name = "name")
                         val name: String = "",
                         @Json(name = "logo")
                         val logo: Logo,
                         @Json(name = "language_id")
                         val languageId: Int = 0,
                         @Json(name = "slug")
                         val slug: String = "",
                         @Json(name = "states")
                         val states: States = States()
)





