package com.ns.news.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Logo(@Json(name = "x1")
                val x1: String = "",
                 @Json(name = "x2")
                val x2: String = "",
                 @Json(name = "x3")
                val x3: String = "")