package com.ns.news.presentation.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.news.NewsApplication
import com.ns.news.R

class OnBoarding1 : Fragment() {

    companion object {
        fun create(value: String): OnBoarding1 {
            val fragment = OnBoarding1()
//            fragment.arguments = value.
            return fragment
        }
    }

    private val viewModel: LanguageViewModel by viewModels { LanguageViewModelFactory(NewsApplication.newsRepository!!) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewStateUpdates();
        requestCoinList();
    }

    /**
     * Observing Language & State Data
     */
    private fun observeViewStateUpdates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect{
               Log.i("Ashwani", "${it.languagePageSubTitle}")

            }
        }
    }

    /**
     * Making API Request to Get Language and State Data
     */
    private fun requestCoinList() {
        viewModel.requestLanguageList()
    }




}