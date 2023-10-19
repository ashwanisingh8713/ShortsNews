package com.ns.shortsnews.ui.fragment

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.R
import com.ns.shortsnews.R.*
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.data.mapper.UserOtp
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.databinding.FragmentLanguageBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.VideoCategory
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.domain.usecase.video_category.UpdateVideoCategoriesUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LanguageFragment : Fragment(layout.fragment_language) {
    lateinit var binding: FragmentLanguageBinding
    private var from: String = ""
    private var selectedLanguages = mutableListOf<String>()
    private val videoCategoryViewModel: VideoCategoryViewModel by activityViewModels {
        VideoCategoryViewModelFactory().apply {
            inject(
                VideoCategoryUseCase(VideoCategoryRepositoryImp(get())),
                UpdateVideoCategoriesUseCase(VideoCategoryRepositoryImp(get()))
            )
        }
    }
    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory().apply {
            inject(
                LanguageDataUseCase(UserDataRepositoryImpl(get())),
                UserSelectionsDataUseCase(UserDataRepositoryImpl(get()))
            )
        }
    }

    private var selectedNumbers = 0
    private var selectedItemList = mutableListOf<LanguageData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguageBinding.bind(view)
        AppPreference.initLanguage(requireActivity())

        // Get selected languages from preference
        selectedLanguages = AppPreference.getSelectedLanguages()

        if (NetworkXProvider.isInternetConnected) {
            userViewModel.requestLanguagesApi()
        } else {
            showTryAgainText()
        }
        from = arguments?.getString("from").toString()
        if (AppPreference.isLanguageSelected) {
            binding.continueButton.text = "Save"
        }

        // Language API Error Listener
        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.errorState.filterNotNull().collectLatest {
                hideTryAgainText()
                Alert().showErrorDialog(AppConstants.API_ERROR_TITLE, AppConstants.API_ERROR, requireActivity())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.LanguagesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh", "Language onSuccess ::: $it")
                it.let {
                    if (it.isNotEmpty()) {
                        setupChipGroup(it)
                        selectedItemList.addAll(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarLanguages.visibility = View.VISIBLE
                } else {
                    binding.progressBarLanguages.visibility = View.GONE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().filter {
                it.videoCategories.isNotEmpty()
            }.collectLatest {
                // Save category data in preference
                getSelectedVideoInterstCategory(it.videoCategories as MutableList<VideoCategory>)
                if (from == AppConstants.FROM_EDIT_PROFILE) {
                    activity?.finish()
                } else {
                    AppPreference.isRefreshRequired = false
                    val bundle = Bundle()
                    bundle.putString("name", /*it.name*/"")
                    userViewModel.updateFragment(UserViewModel.MAIN_ACTIVITY, bundle)
                }

            }
        }


        // Video Category Error Listener
        viewLifecycleOwner.lifecycleScope.launch {
            videoCategoryViewModel.errorState.filterNotNull().collectLatest {
                    Alert().showErrorDialog(AppConstants.API_ERROR_TITLE, it, requireActivity())
            }
        }

        binding.submitButtonConst.setOnClickListener {
            if (selectedNumbers > 0) {
                if (from == AppConstants.FROM_EDIT_PROFILE) {
                    if (selectedItemList.isNotEmpty()) {
                        AppPreference.isLanguageSelected = true
                        AppPreference.saveSelectedLanguagesToPreference(selectedLanguages)
                        AppPreference.isRefreshRequired = true
                        AppPreference.isInterestUpdateNeeded = true
                        videoCategoryViewModel.loadVideoCategory()
                    }

                } else {
                    if (selectedItemList.isNotEmpty()) {
                        AppPreference.saveSelectedLanguagesToPreference(selectedLanguages)
                        videoCategoryViewModel.loadVideoCategory()
                    }
                }
            } else {
                Alert().showGravityToast(requireActivity(), AppConstants.AT_LEAST_SELECT_ONE_LANGUAGE)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (selectedNumbers < 1){
                Alert().showGravityToast(requireActivity(), AppConstants.AT_LEAST_SELECT_ONE_LANGUAGE)
            } else {
             activity?.finish()
            }
        }
    }

    private fun setupChipGroup(optionList: List<LanguageData>) {
        val idList = mutableListOf<Int>()
        for (chipData in optionList) {
            idList.clear()
            val mChip =
                this.layoutInflater.inflate(layout.item_chip_view, null, false) as Chip
            mChip.text = chipData.name
            mChip.isCheckable = true
            mChip.isClickable = true
            mChip.isChipIconVisible = true
            mChip.chipStrokeWidth = 4F
            mChip.checkedIcon = ContextCompat.getDrawable(requireContext(), drawable.check)
            binding.choiceChipGroup.isSingleSelection = false
            binding.choiceChipGroup.isClickable = true
            binding.choiceChipGroup.addView(mChip)

            if (chipData.default_select) {
                mChip.chipIcon = ContextCompat.getDrawable(requireContext(), drawable.check)
                mChip.chipBackgroundColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color.white))
                mChip.setChipBackgroundColorResource(color.white)
                mChip.setTextColor(ContextCompat.getColor(requireContext(), color.black))
                mChip.isChecked = true
                selectedNumbers++
            } else {
                mChip.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        color.black
                    )
                )
                mChip.chipIcon = ContextCompat.getDrawable(requireContext(), drawable.uncheck)
                mChip.setTextColor(ContextCompat.getColor(requireContext(), color.white))
                mChip.isChecked = false
                mChip.chipStrokeWidth = 4F
                mChip.chipStrokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color.white))
            }

            mChip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), drawable.check)
                    mChip.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            color.white
                        )
                    )

                    mChip.setTextColor(ContextCompat.getColor(requireContext(), color.black))
                    updateSelectedItem(chipData.id, chipData.name, chipData.slug, true, "", true)
                    createSelectedLanguagesValue(chipData.id)
                    selectedNumbers++
                } else {
                    mChip.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), drawable.uncheck)
                    mChip.setTextColor(ContextCompat.getColor(requireContext(), color.white))
                    mChip.chipStrokeWidth = 4F
                    mChip.chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            color.white
                        )
                    )
                    updateSelectedItem(chipData.id, chipData.name, chipData.slug, false, "", false)
                    createSelectedLanguagesValue(chipData.id)
                    selectedNumbers--
                }
            }
        }
    }

    private fun getSelectedVideoInterstCategory(categoryList:MutableList<VideoCategory>){
        val unselectedCategory = mutableListOf<VideoCategory>()
        val selectedCategory = mutableListOf<VideoCategory>()

        for (item in categoryList){
            if (item.default_select){
                selectedCategory.add(item)
            } else {
                unselectedCategory.add(item)
            }
        }
        val  finalList:List<VideoCategory> = selectedCategory+unselectedCategory
        CoroutineScope(Dispatchers.IO).launch {
            AppPreference.saveCategoriesToPreference(finalList)
            AppPreference.init(requireActivity())
            AppPreference.isRefreshRequired = true
            AppPreference.init(requireActivity())
            Log.i("cat",AppPreference.categoryList.toString())
        }
    }


    private fun createSelectedLanguagesValue(id: String) {

        if(selectedLanguages.contains(id)) {
            selectedLanguages.remove(id)
        } else {
            selectedLanguages.add(id)
        }
    }

    private fun updateSelectedItem(
        id: String,
        name: String,
        slug: String,
        selected: Boolean,
        icon: String,
        defaultSelected:Boolean
    ) {
        for (item in selectedItemList) {
            if (item.id == id) {
                item.isSelected = selected
                item.name = name
                item.slug = slug
                item.icon = icon
                item.default_select = defaultSelected
            }
        }
    }

    private fun showTryAgainText() {
        binding.noNetworkParent.visibility = View.VISIBLE
        binding.tryAgain.visibility = View.VISIBLE
        binding.tryAgain.setOnClickListener {
            if (NetworkXProvider.isInternetConnected) {
                userViewModel.requestLanguagesApi()
                hideTryAgainText()
            } else {
                NoConnection.noConnectionSnackBarInfinite(
                    binding.root,
                    requireContext() as AppCompatActivity
                )
            }
        }
    }

    private fun hideTryAgainText() {
        binding.tryAgain.visibility = View.GONE
        binding.noNetworkImg.visibility = View.GONE
    }
}