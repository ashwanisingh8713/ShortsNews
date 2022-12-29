package com.ns.news.presentation.activity.ui.shorts.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleShortsData(
    val articleImage:String,
    val section:String,
    val timeStamp:String,
    val articleTitle:String,
    val shortDes:List<String>
): Parcelable
