package com.news.data.api.model


import com.ns.news.domain.Result
import com.ns.news.data.api.model.Logo
import com.squareup.moshi.Json

data class SectionItem(@Json(name = "section_id")
                        val id: Int = 0,
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
            id: Int, api: String, name: String,
            inHamburger: Boolean, inBreadcrumb: Boolean, logo: Logo, subSections: List<SectionItem>?
        ): Result<SectionItem> {
            return Result{
                requireNotNull(id)
                requireNotNull(api)
                SectionItem(
                    id = id,
                    api = api,
                    name = name,
                    inBreadcrumb = inBreadcrumb,
                    inHamburger = inHamburger,
                    logo = logo,
                    subSections = subSections
                )
            }

        }
    }

}


data class Data(@Json(name = "sections")
                val sections: List<SectionItem>?)


data class SectionResponse(@Json(name = "msg")
                   val msg: String = "",
                   @Json(name = "data")
                   val data: Data,
                   @Json(name = "status")
                   val status: Boolean = false)





