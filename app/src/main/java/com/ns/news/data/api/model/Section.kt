package com.ns.news.data.api.model


import com.ns.news.data.db.Section
import com.ns.news.domain.Result
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SectionItem(@Json(name = "section_id")
                        val sectionId: String = "",
                       @Json(name = "api")
                       val api: String = "",
                       @Json(name = "name")
                       val name: String = "",
                       @Json(name = "show_in_hamburger")
                       val inHamburger: Boolean = false,
                       @Json(name = "show_in_breadcrumbs")
                       val inBreadcrumb: Boolean = false,
                       @Json(name = "logo")
                       val logo: Logo,
                       @Json(name = "subSections")
                       val subSections: List<SectionItem>? = listOf(),
                       @Json(name = "slug")
                       val slug: String = "") {


    companion object {
        fun of(
            sectionId: String, api: String, name: String,
            inHamburger: Boolean, inBreadcrumb: Boolean, logo: Logo, subSections: List<SectionItem>?
        ): Result<SectionItem> {
            return Result{
                SectionItem(
                    sectionId = sectionId,
                    api = api,
                    name = name,
                    inBreadcrumb = inBreadcrumb,
                    inHamburger = inHamburger,
                    logo = logo,
                    subSections = subSections
                )
            }

        }
        fun of(
            sectionId: String, api: String, name: String,
            inHamburger: Boolean, inBreadcrumb: Boolean, logo: Logo, subSections: List<SectionItem>, slug: String
        ): Section {
            return Section(
                    sectionId = sectionId,
                    api = api,
                    name = name,
                    inBreadcrumb = inBreadcrumb,
                    inHamburger = inHamburger,
                    logo = logo,
                    subSections = subSections,
                    slug = slug
                )
        }
    }

}

@JsonClass(generateAdapter = true)
data class Data(@Json(name = "sections")
                val sections: List<SectionItem>?)

@JsonClass(generateAdapter = true)
data class SectionResponse(@Json(name = "msg")
                   val msg: String = "",
                           @Json(name = "data")
                   val data: Data,
                           @Json(name = "status")
                   val status: Boolean = false)





