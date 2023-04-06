package com.ns.shortsnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ns.shortsnews.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.topAppBar.setNavigationOnClickListener {
            this.onBackPressed()
        }
    }
}