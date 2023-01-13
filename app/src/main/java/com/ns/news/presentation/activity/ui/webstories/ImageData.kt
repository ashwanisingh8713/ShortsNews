package com.ns.news.presentation.activity.ui.webstories

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageData(
    val imageUrl:String,
    val title:String
):Parcelable
