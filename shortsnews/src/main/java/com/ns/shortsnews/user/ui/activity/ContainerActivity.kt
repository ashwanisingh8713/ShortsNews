package com.ns.shortsnews.user.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ActivityContainerBinding
import com.ns.shortsnews.user.ui.fragment.EditProfileFragment
import com.ns.shortsnews.user.ui.fragment.FollowingFragment
import com.ns.shortsnews.user.ui.fragment.PersonaliseFragment

class ContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        val view = binding.root
        window.statusBarColor = Color.parseColor("#000000")
        window.navigationBarColor = Color.parseColor("#000000")
        when(intent.getStringExtra("to")){
             "per" -> {
                launchPersonaliseFragment()
             }
            "fol" -> {
               launchFollowingFragment()
            }
            "edit_profile" -> {
                launchEditProfileFragment()
            }
        }

        setContentView(view)

    }
    private fun launchPersonaliseFragment(){
        val fragment = PersonaliseFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fc, fragment).commit()

    }

    private fun launchFollowingFragment(){
        val fragment = FollowingFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fc, fragment).commit()
    }

    private fun launchEditProfileFragment() {
        val fragment = EditProfileFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fc, fragment)
            .commit()
    }
}