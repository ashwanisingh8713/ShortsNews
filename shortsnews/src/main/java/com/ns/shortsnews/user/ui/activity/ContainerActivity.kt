package com.ns.shortsnews.user.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.ActivityContainerBinding
import com.ns.shortsnews.user.ui.fragment.FollowingFragment
import com.ns.shortsnews.user.ui.fragment.PersonaliseFragment

class ContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        val view = binding.root
        val data = intent.getStringExtra("to")
        when(data){
             "per" -> {
                launchPersonaliseFragment()
             }
            "fol" -> {
               launchFollowingFragment()
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
}