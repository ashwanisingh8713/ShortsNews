package com.ns.news.presentation.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.news.R
import com.ns.news.data.api.model.DataItem
import com.ns.news.databinding.FragmentOnboarding2Binding
import com.ns.news.databinding.StatesOnboardingRowBinding
import com.ns.news.utils.ImageUtils
import com.ns.view.flowlayout.FlowLayout
import com.ns.view.flowlayout.TagAdapter

class OnBoarding2 : Fragment() {
    private val sharedSectionViewModel: OnBoardingSharedViewModel by activityViewModels { OnBoardingSharedViewModelFactory() }
    private lateinit var binding: FragmentOnboarding2Binding

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
        binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewStateUpdates()
        requestLanguageList()
        observeSelectedLanguage()

    }

    private fun observeViewStateUpdates() {

    }

    private fun requestLanguageList() {


    }

    override fun onResume() {
        super.onResume()
        (activity as OnBoardingActivity).buttonsVisibility(View.VISIBLE)
    }

    /**
     * Observing Language & State Data
     */
    private fun observeSelectedLanguage() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedSectionViewModel.onBoardingSharedViewModel.collect {
                binding.onboardingTitle.text = it.states.statesPageTitle
                binding.onboardingSubtitle.text = it.states.statesPageSubTitle

                binding.idFlowlayout.setTagAdapter(object : TagAdapter<DataItem>(it.states.data) {
                    override fun getView(
                        parent: FlowLayout?,
                        position: Int,
                        stateItem: DataItem
                    ): View {
                        val itemBinding: StatesOnboardingRowBinding =
                            StatesOnboardingRowBinding.inflate(
                                layoutInflater,
                                binding.idFlowlayout,
                                false
                            )

                        itemBinding.textviewSates.text = stateItem.name

                        itemBinding.statesRow.setOnClickListener {
                            if (stateItem.is_selected) {
                                stateItem.defaultSelection = true

                                // for background UI changes
                                stateItem.is_selected = false
                                itemBinding.selectItem.setBackgroundResource(R.drawable.onboarding_unselected_plus)
                            } else {
                                stateItem.defaultSelection = false

                                // for background UI changes
                                stateItem.is_selected = true
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

}