package com.ns.shortsnews.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLanguageBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.LanguageTable
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

class LanguageFragment : Fragment(R.layout.fragment_language) {
    lateinit var binding: FragmentLanguageBinding
    private var from: String = ""
    private var selectedLanguages = ""
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
                UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
                UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
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
        Log.i("language","$AppPreference.selectedLanguages!!")
        selectedLanguages = AppPreference.selectedLanguages!!
        if (NetworkXProvider.isInternetConnected) {
            userViewModel.requestLanguagesApi()
        } else {
            NoConnection.noConnectionSnackBarInfinite(
                binding.root,
                requireContext() as AppCompatActivity
            )
        }
        from = arguments?.getString("from").toString()
        if (AppPreference.isLanguageSelected) {
            binding.continueButton.text = "Save"
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarLanguages.visibility = View.GONE
                Log.i("kamlesh", "OTPFragment onError ::: $it")
            }
        }


        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.LanguagesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh", "Language onSuccess ::: $it")
                it.let {
                    binding.progressBarLanguages.visibility = View.GONE
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
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().filter {
                it.videoCategories.isNotEmpty()
            }.collectLatest {
                // Save category data in preference
                getSelectedVideoInterstCategory(it.videoCategories as MutableList<VideoCategory>)
                if (from == AppConstants.FROM_EDIT_PROFILE){
                    activity?.finish()
                } else {
                    AppPreference.isRefreshRequired = false
                    val bundle = Bundle()
                    bundle.putString("name", /*it.name*/"")
                    userViewModel.updateFragment(UserViewModel.MAIN_ACTIVITY, bundle)
                }

            }
        }


        binding.submitButtonConst.setOnClickListener {
            if (selectedNumbers > 0) {
                if (from == AppConstants.FROM_EDIT_PROFILE) {
                    if (selectedItemList.isNotEmpty()) {
                        AppPreference.isLanguageSelected = true
                        AppPreference.selectedLanguages = selectedLanguages.dropLast(1)
                        AppPreference.isRefreshRequired = true
                        AppPreference.isInterestUpdateNeeded = true
                        videoCategoryViewModel.loadVideoCategory(AppPreference.selectedLanguages!!)
                    }

                } else {
                    if (selectedItemList.isNotEmpty()) {
                        AppPreference.isLanguageSelected = true
                        AppPreference.selectedLanguages = selectedLanguages.dropLast(1)
                        AppPreference.isRefreshRequired = false
                        videoCategoryViewModel.loadVideoCategory(AppPreference.selectedLanguages!!)
                    }
                }
            } else {
                Alert().showGravityToast(requireActivity(), "Please select one language")
            }
        }
    }

    private fun setupChipGroup(optionList: List<LanguageData>) {
        val idList = mutableListOf<Int>()
        for (chipData in optionList) {
            idList.clear()
            val mChip =
                this.layoutInflater.inflate(R.layout.item_chip_view, null, false) as Chip
            mChip.text = chipData.name
            mChip.isCheckable = true
            mChip.isClickable = true
            mChip.isChipIconVisible = true
            mChip.chipStrokeWidth = 4F
            mChip.checkedIcon = ContextCompat.getDrawable(requireContext(), R.drawable.check)
            binding.choiceChipGroup.isSingleSelection = false
            binding.choiceChipGroup.isClickable = true
            binding.choiceChipGroup.addView(mChip)

            if (chipData.default_select) {
                mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.check)
                mChip.chipBackgroundColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                mChip.setChipBackgroundColorResource(R.color.white)
                mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                mChip.isChecked = true
                selectedNumbers++
            } else {
                mChip.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        com.videopager.R.color.black
                    )
                )
                mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck)
                mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                mChip.isChecked = false
                mChip.chipStrokeWidth = 4F
                mChip.chipStrokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            }

            mChip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.check)
                    mChip.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )

                    mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    updateSelectedItem(chipData.id, chipData.name, chipData.slug, true, "", true)
                    createSelectedLanguagesValue(chipData.id)
                    selectedNumbers++
                } else {
                    mChip.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            com.videopager.R.color.black
                        )
                    )
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck)
                    mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    mChip.chipStrokeWidth = 4F
                    mChip.chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
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

    private fun comparePrefereceServer(interestsList: MutableList<VideoCategory>,
                                       interestsDataList:MutableList<VideoCategory> ):MutableList<VideoCategory>{
        var finalList:MutableList<VideoCategory> = mutableListOf()
        if (interestsDataList.isEmpty()){
            finalList.addAll(interestsList)
        } else {
            for (interestsData in interestsList) {
                for (interests in interestsDataList) {
                    if (interestsData.id == interests.id) {
                        var convertedData = VideoCategory(
                            interests.id,
                            interests.name,
                            interests.selected,
                            interests.icon,
                            interests.default_select
                        )
                        finalList.add(convertedData)
                    }
                }
            }
        }
        return finalList
    }
    private fun createSelectedLanguagesValue(id: String) {
        val tempValue = "$id,"
        if (selectedLanguages.isNotEmpty()) {
            if (selectedLanguages.last() != ',') {
               selectedLanguages = selectedLanguages + ","
            }
        }
        var modifiedString = ""
        if (selectedLanguages.contains(tempValue)) {
            modifiedString = selectedLanguages.replace(tempValue, "")
            selectedLanguages = modifiedString
        } else {
            selectedLanguages += tempValue
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
}