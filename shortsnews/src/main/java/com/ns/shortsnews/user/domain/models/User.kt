package com.ns.shortsnews.user.domain.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val status:Boolean,
    val msg:String
)
