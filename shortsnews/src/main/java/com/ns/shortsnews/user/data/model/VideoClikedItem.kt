package com.ns.shortsnews.user.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Ashwani Kumar Singh on 23,May,2023.
 */
@Parcelize
data class VideoClikedItem(val requiredId: String, val selectedPosition: Int, val videoFrom: String) :
    Parcelable {

}
