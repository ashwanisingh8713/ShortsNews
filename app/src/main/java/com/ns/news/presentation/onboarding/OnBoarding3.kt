package com.ns.news.presentation.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.news.data.api.model.Category
import com.ns.news.NewsApplication
import com.ns.news.R
import com.ns.news.data.api.model.DataItem
import com.ns.news.databinding.CategoryOnboardingRowBinding
import com.ns.news.databinding.FragmentOnboarding3Binding
import com.ns.news.databinding.StatesOnboardingRowBinding
import com.ns.view.flowlayout.FlowLayout
import com.ns.view.flowlayout.TagAdapter

class OnBoarding3 : Fragment() {
    private lateinit var binding: FragmentOnboarding3Binding

    companion object {
        fun create(value: String): OnBoarding3 {
            val fragment = OnBoarding3()
//            fragment.arguments = value.
            return fragment
        }
    }

    private val viewModel: TopicViewModel by viewModels {
        TopicViewModelFactory(
            NewsApplication.newsRepository!!
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestCoinList()
        observeViewStateUpdates()
        binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    /**
     * Observing Language & State Data
     */
    private fun observeViewStateUpdates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                binding.idFlowlayout.setTagAdapter(object : TagAdapter<Category>(it.categories) {
                    override fun getView(
                        parent: FlowLayout?,
                        position: Int,
                        categoryItem: Category
                    ): View {
                        val itemBinding: CategoryOnboardingRowBinding =
                            CategoryOnboardingRowBinding.inflate(
                                layoutInflater,
                                binding.idFlowlayout,
                                false
                            )

                        itemBinding.textViewCategory.text = categoryItem.name
                        itemBinding.categoryRow.setOnClickListener {
                            if (categoryItem.is_selected) {
                                categoryItem.defaultSelection = true

                                // for background UI changes
                                categoryItem.is_selected = false
                                itemBinding.selectItem.setBackgroundResource(R.drawable.onboarding_unselected_plus)
                            } else {
                                categoryItem.defaultSelection = false

                                // for background UI changes
                                categoryItem.is_selected = true
                                itemBinding.selectItem.setBackgroundResource(R.drawable.onboarding_selected_check)
                            }
                        }

//                        itemBinding.languageRow.setOnClickListener {
//                            sharedSectionViewModel.setLanguageSelection(languageItem)
//                        }
                        return itemBinding.root
                    }

                })


            }
        }
    }

    /**
     * Making API Request to Get Language and State Data
     */
    private fun requestCoinList() {
        viewModel.requestTopicData()
    }

    override fun onResume() {
        super.onResume()
        (activity as OnBoardingActivity).buttonsVisibility(View.VISIBLE)
    }


}