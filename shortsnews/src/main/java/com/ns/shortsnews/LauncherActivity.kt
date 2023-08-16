package com.ns.shortsnews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ns.shortsnews.utils.AppPreference

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (!AppPreference.isUserLoggedIn){
            launchProfileActivity()
        } else {
            if (AppPreference.isLanguageSelected) {
                if (intent?.extras != null){
                    Log.i("intent_newLaunch","get intent data on Create Main activity" + intent.extras.toString())
                    launchMainActivityWithExtras(intent)
                } else {
                   launchMainActivity()
                }
            } else {
                launchProfileActivity()
            }
        }
    }

    private fun launchMainActivityWithExtras(data: Intent?) {
        val intent = Intent(this, MainActivity::class.java)
        val id = intent.putExtra("videoId",data?.getStringExtra("id").toString())
        val type = intent.putExtra("type",data?.getStringExtra("type").toString())
        val previewUrl = intent.putExtra("preview_url",data?.getStringExtra("videoPreviewUrl").toString())
        val video_url = intent.putExtra("video_url", data?.getStringExtra("video_url").toString())
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        Log.i("intent_newLaunch","From profile activity :: $id $type $previewUrl $video_url")
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