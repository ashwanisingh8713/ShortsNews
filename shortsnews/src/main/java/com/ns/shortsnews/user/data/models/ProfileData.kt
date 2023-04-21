package com.ns.shortsnews.user.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileData(
    val userName:String,
    val choseList: List<CategoryList>

)

@JsonClass(generateAdapter = true)
data class CategoryList(
    val title:String,
    val urlEndPoint:String,
    val isSelected:Boolean = false
)
