package com.ns.shortsnews.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ns.shortsnews.MainActivity
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.PlainVideoActivity
import com.ns.shortsnews.ProfileActivity
import com.ns.shortsnews.data.model.VideoClikedItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Ashwani Kumar Singh on 23,May,2023.
 */
class IntentLaunch {

    companion object {

        fun launchMainActivityFinishCurrent(context: Activity) {
            Log.i("lifecycle", "Profile activity on main activity called")
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
        fun launchPlainVideoPlayer(videoItemClick: VideoClikedItem, context: Context) {
            val intent = Intent(context, PlainVideoActivity::class.java)
            intent.putExtra(PlainVideoActivity.KEY_VIDEO_CLICKED_ITEM, videoItemClick)
            context.startActivity(intent)
        }

        fun loginActivityIntent(context: Context) {
            AppPreference.clear()
            val i = Intent(context, ProfileActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(i)
        }

        fun logoutInfoDialog() {
            AppPreference.clear()
            CoroutineScope(Dispatchers.Main).launch {
                val alertDialog =
                    MaterialAlertDialogBuilder(MainApplication.instance!!.getActiveActivity()!!)
                alertDialog.apply {
                    setCancelable(false)
                    this.setTitle("Your session has expired.")
                    this.setMessage("Please login again to continue.")
                    this.setPositiveButton("Logout") { dialog, _ ->
                        // Relaunch Login Activity
                        loginActivityIntent(MainApplication.instance!!.getActiveActivity()!!)
                    }

                    this.show()
                }
            }
        }
    }
}