package com.example.videopager.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.ns.news.presentation.activity.ui.shorts.video.di.MainModule
import com.ns.news.databinding.MainActivityBinding
import com.ns.news.utils.CommonFunctions
import com.videopager.ui.VideoPagerFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Manual dependency injection
        val module = MainModule(this)
        supportFragmentManager.fragmentFactory = module.fragmentFactory

        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpUi(binding)
        binding.retryButton.setOnClickListener {
            setUpUi(binding)
        }
    }

    fun setUpUi(binding: MainActivityBinding){
        if (CommonFunctions.checkForInternet(this)){
                supportFragmentManager.commit {
                    replace<VideoPagerFragment>(binding.fragmentContainer.id)
                }
        } else{
            binding.internetConnectionTv.visibility  = View.VISIBLE
            binding.retryButton.visibility = View.VISIBLE
            binding.fragmentContainer.visibility = View.GONE
        }
    }

}
