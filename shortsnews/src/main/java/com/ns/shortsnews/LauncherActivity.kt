package com.ns.shortsnews

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ns.shortsnews.utils.AppPreference

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (!AppPreference.isUserLoggedIn){
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            if (AppPreference.isLanguageSelected) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this,ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}