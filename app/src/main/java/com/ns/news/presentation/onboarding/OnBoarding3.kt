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

class OnBoarding3 : Fragment() {

    companion object {
        fun create(value: String): OnBoarding3 {
            val fragment = OnBoarding3()
//            fragment.arguments = value.
            return fragment
        }
    }

    private val viewModel: TopicViewModel by viewModels { TopicViewModelFactory(
        NewsApplication.newsRepository!!) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestCoinList()
        observeViewStateUpdates()
        return inflater.inflate(R.layout.fragment_onboarding_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    /**
     * Observing Language & State Data
     */
    private fun observeViewStateUpdates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect{
                Log.i("Ashwani", "${it.categories}")

            }
        }
    }

    /**
     * Making API Request to Get Language and State Data
     */
    private fun requestCoinList() {
        viewModel.requestTopicData()
    }



}