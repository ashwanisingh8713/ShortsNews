package com.ns.shortsnews.utils

import android.content.Context
import android.content.Intent
import com.ns.shortsnews.PlainVideoActivity
import com.ns.shortsnews.user.data.model.VideoClikedItem

/**
 * Created by Ashwani Kumar Singh on 23,May,2023.
 */
class IntentLaunch {

    companion object {
        fun launchPlainVideoPlayer(videoItemClick: VideoClikedItem, context: Context) {
            val intent = Intent(context, PlainVideoActivity::class.java)
            intent.putExtra(PlainVideoActivity.KEY_VIDEO_CLICKED_ITEM, videoItemClick)
            context.startActivity(intent)
        }
    }
}