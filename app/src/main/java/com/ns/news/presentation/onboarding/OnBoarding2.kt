package com.ns.news.presentation.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.news.presentation.viewmodel.LanguageViewModel
import com.news.presentation.viewmodel.LanguageViewModelFactory
import com.ns.news.R

class OnBoarding2 : Fragment() {

    companion object {
        fun create(value: String): OnBoarding2 {
            val fragment = OnBoarding2()
//            fragment.arguments = value.
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }



}