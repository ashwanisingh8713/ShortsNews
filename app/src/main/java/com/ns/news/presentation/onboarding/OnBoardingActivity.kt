package com.ns.news.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ns.news.databinding.ActivityOnboardingBinding
import com.ns.news.presentation.activity.BottomNavActivity
import com.ns.news.presentation.activity.ui.search.SearchActivity
import com.ns.news.presentation.activity.ui.settings.SettingsActivity

class OnBoardingActivity : AppCompatActivity() {

    private val sharedSectionViewModel: OnBoardingSharedViewModel by viewModels { OnBoardingSharedViewModelFactory() }
    private lateinit var binding: ActivityOnboardingBinding
    private var isStates: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeSelectedLanguage()


        // To Enable or Disable swipe of Pager
        binding.pager2.isUserInputEnabled = true;
        binding.pager2.offscreenPageLimit = 4

        binding.pager2.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
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

        // disabling fragments swapping
        binding.pager2.isUserInputEnabled = false

        // Skip button event
        binding.skipBtn.setOnClickListener {
            //redirectSkipScreens(binding.pager2.currentItem)
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Back button event
        binding.buttonFeedBack.setOnClickListener {
            redirectBackScreens(binding.pager2.currentItem)
        }

        // Next button event
        binding.buttonFeedNext.setOnClickListener {
            redirectNextScreens(binding.pager2.currentItem)
        }


    }

    /**
     * Skip Redirection fragments with positions
     */
    private fun redirectSkipScreens(position: Int) {
        when (position) {
            1 -> binding.pager2.currentItem = 2
            2 -> startActivity(Intent(this, BottomNavActivity::class.java))
            else -> startActivity(Intent(this, BottomNavActivity::class.java))
        }
    }

    /**
     * Next Redirection fragments with positions
     */
    private fun redirectNextScreens(position: Int) {
        when (position) {
            1 -> binding.pager2.currentItem = 2
            2 -> binding.pager2.currentItem = 3
            else -> binding.pager2.currentItem = 0
        }
    }

    /**
     * Back Redirection fragments with positions
     */
    private fun redirectBackScreens(position: Int) {
        when (position) {
            1 -> binding.pager2.currentItem = 0
            2 -> {
                if (isStates) binding.pager2.currentItem = 1
                else binding.pager2.currentItem = 0
            }
            else -> binding.pager2.currentItem = 0
        }
    }

    /**
     * Observing Language & State Data
     */
    private fun observeSelectedLanguage() {
        lifecycleScope.launchWhenStarted {
            sharedSectionViewModel.onBoardingSharedViewModel.collect {
                if (it.states.data?.size == 0) {
                    isStates = false
                    binding.pager2.currentItem = 2
                } else {
                    isStates = true
                    binding.pager2.currentItem = 1
                }

            }
        }
    }


    fun buttonsVisibility(value: Int) {
        binding.buttonFeedBack.visibility = value
        binding.buttonFeedNext.visibility = value
        binding.skipBtn.visibility = value
    }


}