package com.ns.shortsnews.utils

import android.graphics.Bitmap
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.palette.graphics.Palette
import com.videopager.vm.VideoSharedEventViewModel


class AppConstants {

    companion object {
        // Toast constants

        const val  FILL_REQUIRED_FIELD = "Please fill the fields"
        const val  FILL_VALID_EMAIL = "Please enter valid email address "
        const val AT_LEAST_SELECT_ONE_CATEGORY = "Please select at-least one category"
        const val AT_LEAST_SELECT_ONE_LANGUAGE = "Please select at-least one language"
        const val CATEGORY_UPDATE_SUCCESS = "Category preferences set successfully"
        const val  FILL_EMAIL = "Please enter your email address "
        const val  FILL_OTP = "Please enter OTP"
        const val  FILL_NAME = "Please enter your name"
        const val  FILL_VALID_OTP = "Please enter valid OTP"
        const val  SPRINT_TWO = "Coming in sprint 2nd"
        const val API_ERROR = "Try Again."
        const val TECH_ERROR = "Technical error"


        // Toast MSG text
        const val API_ERROR_TITLE = "Something went wrong"

        // Arguments values
        const val FROM_EDIT_PROFILE = "from_edit_profile"
        const val FROM_PROFILE = "from_profile"

        // PendingIntent

        const val ID = "id"
        const val TYPE = "type"
        const val VIDEO_PREVIEW_URL = "videoPreviewUrl"
        const val VIDEO_URL = "video_url"


    }
}

/*Bottom sheet pallet color using bitmap icon from response URL*/
internal fun setBottomSheetHeaderBg(bitmap: Bitmap, layout: ConstraintLayout, sharedEventViewModel: VideoSharedEventViewModel? = null) {
    layout.alpha = 1.0f

    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    Palette.from(mutableBitmap).generate { palette ->

        val lightVibrantSwatch = palette?.lightVibrantSwatch?.rgb
        lightVibrantSwatch?.let {
            layout.setBackgroundColor(
                lightVibrantSwatch
            )
            sharedEventViewModel?.sendPaletteColor(lightVibrantSwatch)
        }

        val vibrantSwatch = palette?.vibrantSwatch?.rgb
        vibrantSwatch?.let {
            layout.setBackgroundColor(vibrantSwatch)
            sharedEventViewModel?.sendPaletteColor(vibrantSwatch)
        }

        val lightMutedSwatch = palette?.lightMutedSwatch?.rgb
        lightMutedSwatch?.let {
            layout.setBackgroundColor(lightMutedSwatch)
            sharedEventViewModel?.sendPaletteColor(lightMutedSwatch)
        }

        val mutedSwatch = palette?.mutedSwatch?.rgb
        mutedSwatch?.let {
            layout.setBackgroundColor(mutedSwatch)
            sharedEventViewModel?.sendPaletteColor(mutedSwatch)
        }

        val darkMutedSwatch = palette?.darkMutedSwatch?.rgb
        darkMutedSwatch?.let {
            layout.setBackgroundColor(darkMutedSwatch)
            sharedEventViewModel?.sendPaletteColor(darkMutedSwatch)
        }

        val darkVibrantSwatch = palette?.darkVibrantSwatch?.rgb
        darkVibrantSwatch?.let {
            layout.setBackgroundColor(
                darkVibrantSwatch
            )
            sharedEventViewModel?.sendPaletteColor(darkVibrantSwatch)
        }

    }
}