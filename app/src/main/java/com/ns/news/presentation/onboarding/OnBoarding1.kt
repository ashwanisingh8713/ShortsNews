package com.ns.news.presentation.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.news.NewsApplication
import com.ns.news.R
import com.ns.news.data.api.model.LanguagesItem
import com.ns.news.databinding.FragmentOnboarding1Binding
import com.ns.news.databinding.LaguageOnboardingRowBinding
import com.ns.view.flowlayout.FlowLayout
import com.ns.view.flowlayout.TagAdapter


class OnBoarding1 : Fragment() {

    private lateinit var binding: FragmentOnboarding1Binding
    private val sharedSectionViewModel: OnBoardingSharedViewModel by activityViewModels { OnBoardingSharedViewModelFactory() }

    companion object {
        fun create(value: String): OnBoarding1 {
            val fragment = OnBoarding1()
//            fragment.arguments = value.
            return fragment
        }
    }

    private val viewModel: LanguageViewModel by viewModels {
        LanguageViewModelFactory(
            NewsApplication.newsRepository!!
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewStateUpdates()
        requestLanguageList()
    }

    /**
     * Observing Language & State Data
     */
    private fun observeViewStateUpdates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                binding.onboardingTitle.text = it.languagePageTitle
                binding.onboardingSubtitle.text = it.languagePageSubTitle

                binding.idFlowlayout.setTagAdapter(object :
                    TagAdapter<LanguagesItem>(it.languages) {
                    override fun getView(
                        parent: FlowLayout?,
                        position: Int,
                        languageItem: LanguagesItem
                    ): View {
                        val itemBinding: LaguageOnboardingRowBinding =
                            LaguageOnboardingRowBinding.inflate(
                                layoutInflater,
                                binding.idFlowlayout,
                                false
                            )

                        itemBinding.languageTextView.text = languageItem.name

                        itemBinding.languageRow.setOnClickListener {
                            /*if (languageItem.is_selected) {
                                languageItem.is_selected = false
                                itemBinding.checkImageview.setBackgroundResource(R.drawable.onboarding_selected_check)
                            } else {
                                languageItem.is_selected = true
                                itemBinding.checkImageview.setBackgroundResource(R.drawable.onboarding_unselected_gray)
                            }*/
                            sharedSectionViewModel.setLanguageSelection(languageItem)
                        }
                        return itemBinding.root
                    }

                })


            }
        }
    }

    /**
     * Making API Request to Get Language and State Data
     */
    private fun requestLanguageList() {
        viewModel.requestLanguageList()
    }

    override fun onResume() {
        super.onResume()
        (activity as OnBoardingActivity).buttonsVisibility(View.GONE)
    }


}