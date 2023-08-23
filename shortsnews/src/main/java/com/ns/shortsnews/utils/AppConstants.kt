package com.ns.shortsnews.utils

import android.content.Context
import coil.imageLoader
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.VideoPagerViewModelFactory

class AppConstants {

    companion object {
        const val PREF_NAME = "NewsDxShorts"

        // Toast constants

        const val  FILL_REQUIRED_FIELD = "Please fill the fields"
        const val  FILL_VALID_EMAIL = "Please enter valid Email id"
        const val  FILL_OTP = "Please enter OTP"
        const val  FILL_VALID_OTP = "Please enter valid OTP"
        const val  OTP_VALIDATION_ERROR = "Error validating otp try again"
        const val  SPRINT_TWO = "Coming in sprint 2nd"
        const val  SPRINT_THREE = "Coming in phase 3rd"
        const val API_ERROR = "Something went wrong"


        // Toast MSG text
        const val CONNECTIVITY_ERROR_TITLE = "Connectivity Error"
        const val CONNECTIVITY_MSG = "Please ensure you have active internet connection."
        const val API_ERROR_TITLE = "API error"

        // Arguments values
        const val FROM_EDIT_PROFILE = "from_edit_profile"
        const val FROM_PROFILE = "from_profile"

        // PendingIntent

        const val ID = "id"
        const val TYPE = "type"
        const val VIDEO_PREVIEW_URL = "videoPreviewUrl"
        const val VIDEO_URL = "video_url"



        fun makeVideoPagerInstance(requiredId: String, videoFrom: String, context: Context, languages:String, selectedPlay:Int = 0): VideoPagerFragment {
            val appPlayerView =  ExoAppPlayerViewFactory().create(context)
            val vpf =  VideoPagerFragment(
                viewModelFactory = { owner ->
                    VideoPagerViewModelFactory(
                        repository = VideoDataRepositoryImpl(),
                        appPlayerFactory = ExoAppPlayerFactory(
                            context = context, cache = MainApplication.cache, currentMediaItemIndex = selectedPlay
                        ),
                        requiredId = requiredId,
                        videoFrom = videoFrom,
                        languages = languages,
                        selectedPlay = selectedPlay
                    ).create(owner)
                },
                appPlayerView = appPlayerView,
                imageLoader = context.imageLoader,
            )
            return vpf
        }



    }
}