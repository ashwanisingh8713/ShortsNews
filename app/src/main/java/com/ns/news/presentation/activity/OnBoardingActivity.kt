package com.ns.news.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ns.news.presentation.onboarding.OnBoarding1
import com.ns.news.presentation.onboarding.OnBoarding2
import com.ns.news.presentation.onboarding.OnBoarding3
import com.ns.news.R
import com.ns.news.databinding.ActivityBottomNavBinding
import com.ns.news.databinding.ActivityOnboardingBinding

class OnBoardingActivity: AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gotoNavPages.setOnClickListener{
            startActivity(Intent(this, BottomNavActivity::class.java))
        }

        // To Enable or Disable swipe of Pager
        binding.pager2.isUserInputEnabled = true;

        binding.pager2.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> OnBoarding1.create("")
                    1 -> OnBoarding2.create("")
                    2 -> OnBoarding3.create("")
                    else -> Fragment()
                }

            }

            override fun getItemCount(): Int {
                return 3
            }
        }
    }
}