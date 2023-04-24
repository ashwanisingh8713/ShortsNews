package com.ns.shortsnews.user.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ActivityContainerBinding

class ContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

    }
}