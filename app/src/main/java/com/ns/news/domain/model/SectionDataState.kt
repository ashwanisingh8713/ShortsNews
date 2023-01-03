package com.ns.news.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Section (val sectionId: String, val sectionName: String, val sectionApi: String,
                    val hasSubsection: Boolean = false): Parcelable

