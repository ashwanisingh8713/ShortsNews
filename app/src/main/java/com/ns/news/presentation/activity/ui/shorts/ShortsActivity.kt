package com.ns.news.presentation.activity.ui.shorts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.ns.news.databinding.ActivityShortsBinding
import com.ns.news.presentation.activity.ui.fragments.ArticleShortsFragment
import com.ns.news.presentation.activity.ui.fragments.PodcastFragment
import com.ns.news.utils.Constants

class ShortsActivity : AppCompatActivity() {
    lateinit var binding:ActivityShortsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityShortsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val shortOption = intent.getStringExtra(Constants.shortsOptionIntentTag)
        if (shortOption.equals(Constants.shortsArticleIntentTag)){
            val articleShortsFragment = ArticleShortsFragment()
            val t: FragmentTransaction = supportFragmentManager.beginTransaction()
            t.replace(binding.fragmentContainerBottomOption.id, articleShortsFragment).commit()
        } else if (shortOption.equals(Constants.podcastIntentTag)){
            val podcastFragment = PodcastFragment()
            val t: FragmentTransaction = supportFragmentManager.beginTransaction()
            t.replace(binding.fragmentContainerBottomOption.id, podcastFragment).commit()
        }
    }
}