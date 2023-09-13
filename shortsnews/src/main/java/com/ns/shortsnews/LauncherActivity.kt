package com.ns.shortsnews

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.videopager.utils.CategoryConstants

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (!AppPreference.isUserLoggedIn){
            launchProfileActivity()
        } else {
            if (AppPreference.isLanguageSelected) {
//                if (intent?.extras != null){
//                    Log.i("intent_newLaunch","Launcher activity extras is there ")
//                    val videoId = intent.getStringExtra(AppConstants.ID)!!
//                    val type = intent.getStringExtra(AppConstants.TYPE)!!
//                    val previewUrl = intent.getStringExtra(AppConstants.VIDEO_PREVIEW_URL)!!
//                    val videoUrl = intent.getStringExtra(AppConstants.VIDEO_URL)!!
//                    Log.i("intent_newLaunch","Launcher activity extras is $videoId$videoUrl$previewUrl ")
//
//                    launchMainActivityWithExtras( videoId, type, previewUrl, videoUrl)
//                } else {
                   launchMainActivity()
//                }
            } else {
                launchProfileActivity()
            }
        }
    }

    private fun launchMainActivityWithExtras( videoId:String, type:String, previewUrl:String, videoUrl: String) {
        val intent = Intent(this, PlainVideoActivity::class.java)
        intent.putExtra(PlainVideoActivity.KEY_VIDEO_CLICKED_ITEM,
            VideoClikedItem("",0, CategoryConstants.NOTIFICATION_VIDEO_DATA)
        )
        intent.putExtra(AppConstants.ID, videoId)
        intent.putExtra(AppConstants.TYPE, type)
        intent.putExtra(AppConstants.VIDEO_PREVIEW_URL, previewUrl)
        intent.putExtra(AppConstants.VIDEO_URL, videoUrl)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        this.finish()
    }

    private fun launchProfileActivity(){
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun launchMainActivity(){
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        this.finish()
    }
}