package com.ns.shortsnews.user.ui.activity

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ActivityContainerBinding
import com.ns.shortsnews.user.domain.models.ProfileData
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
                val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("profile_data", ProfileData::class.java)
                } else {
                    intent.getParcelableExtra<ProfileData>("profile_data")
                }
                if (userData != null) {
                    launchEditProfileFragment(userData)
                }
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

    private fun launchEditProfileFragment(profileData: ProfileData) {
        val fragment = EditProfileFragment()
        val bundle = Bundle()
        bundle.putParcelable("profile", profileData)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fc, fragment)
            .commit()
    }
}