package com.ns.shortsnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.ns.shortsnews.video.di.MainModule
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.fragment.ProfileFragment
import com.videopager.ui.VideoPagerFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val module = MainModule(this)
    val fragment = ProfileFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportFragmentManager.fragmentFactory = module.fragmentFactory
        loadHomeFragment()
        binding.bottomNavigation.setOnItemSelectedListener {
                  when(it.itemId){
                      R.id.home -> {
                          loadHomeFragment()
                      }
                      R.id.profile -> {
                          loadProfileFragment()
                      }
                      else -> {
                          loadProfileFragment()
                      }
                  }
            true
        }

    }

    private fun loadHomeFragment(){
        supportFragmentManager.commit {
            replace<VideoPagerFragment>(R.id.fragment_container)
        }
    }
   private fun loadProfileFragment(){
       supportFragmentManager.commit {
           replace<ProfileFragment>(R.id.fragment_container)
       }
    }
}