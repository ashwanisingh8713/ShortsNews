package com.ns.shortsnews.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ActivityLanguageContainerBinding
import com.ns.shortsnews.ui.fragment.LanguageFragment
import com.ns.shortsnews.utils.AppConstants

class LanguageContainer : AppCompatActivity() {
    private lateinit var binding:ActivityLanguageContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageContainerBinding.inflate(layoutInflater)
        window.statusBarColor = Color.parseColor("#000000")
        window.navigationBarColor = Color.parseColor("#000000")
        languagesFragment()
        setContentView(binding.root)
    }

    @SuppressLint("CommitTransaction")
    private fun languagesFragment() {
        val fragment = LanguageFragment()
        val bundle = Bundle()
        bundle.putString("from", AppConstants.FROM_EDIT_PROFILE)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.language_container_activity, fragment)
            .commit()
    }
}