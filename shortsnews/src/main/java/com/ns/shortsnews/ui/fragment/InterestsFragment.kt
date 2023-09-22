package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ns.shortsnews.R
import com.ns.shortsnews.data.mapper.UserVideoCategory
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.databinding.FragmentInterestsBinding
import com.ns.shortsnews.domain.models.Categories
import com.ns.shortsnews.domain.models.InterestsTable
import com.ns.shortsnews.domain.models.VideoCategory
import com.ns.shortsnews.domain.repository.InterestsRepository
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.domain.usecase.video_category.UpdateVideoCategoriesUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.get


class InterestsFragment : Fragment(R.layout.fragment_interests) {
    lateinit var binding: FragmentInterestsBinding
    private var selectedNumbers = 0
    private val categoryViewModel: VideoCategoryViewModel by activityViewModels { VideoCategoryViewModelFactory().apply {
        inject(VideoCategoryUseCase(VideoCategoryRepositoryImp(get())), (UpdateVideoCategoriesUseCase(VideoCategoryRepositoryImp(get()))))
    } }

    private var selectedItemList = mutableListOf<VideoCategory>()
    private var selectedCategoriesId = mutableListOf<String>()
    private var isModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (selectedNumbers < 1){
                Alert().showGravityToast(requireActivity(), AppConstants.AT_LEAST_SELECT_ONE)
            } else {
                if (isModified){
                    Alert().showGravityToast(requireActivity(), "Please save selected preferences")
                } else {
                    activity?.finish()
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInterestsBinding.bind(view)
        categoryViewModel.loadVideoCategory()
        binding.backButtonUser.setOnClickListener {
            if (selectedNumbers < 1){
                Alert().showGravityToast(requireActivity(), AppConstants.AT_LEAST_SELECT_ONE)
            } else {
                if (isModified){
                    Alert().showGravityToast(requireActivity(), "Please save selected preferences")
                } else {
                    activity?.finish()
                }
            }
        }



        binding.submitButtonConst.setOnClickListener {
            getSelectedVideoInterstCategory(selectedItemList)
            if (selectedNumbers < 1){
                Alert().showGravityToast(requireActivity(), AppConstants.AT_LEAST_SELECT_ONE)
            } else {
                if (isModified){
                    getSelectedCategoriesId(selectedItemList)
                    val data = HashMap<String, List<String>>()
                    data["categories"] = selectedCategoriesId
                    categoryViewModel.updateCategoriesApi(data)
                } else {
                    activity?.finish()
                }
            }



        }


        if(AppPreference.isLanguageSelected){
            binding.submitButtonPers.text = "Save"
        }


        viewLifecycleOwner.lifecycleScope.launch(){
            categoryViewModel.errorState.filterNotNull().collectLatest {
                if(it != "NA"){
                    binding.progressBarPer.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            categoryViewModel.videoCategorySuccessState.filterNotNull().collectLatest {
                it.let {
                    binding.progressBarPer.visibility = View.GONE
                    if (it.videoCategories.isNotEmpty()) {
                        setupChipGroup(it.videoCategories as MutableList<VideoCategory>)
                            selectedItemList.addAll(it.videoCategories)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            categoryViewModel.updateCategoriesSuccessState.filterNotNull().collectLatest {
                it.let {
                    if (it.status){
                        Alert().showGravityToast(requireActivity(), "Category preferences successfully")
                        AppPreference.isRefreshRequired = true
                        activity?.finish()
                    } else {
                        binding.progressBarPer.visibility = View.GONE
                        Alert().showGravityToast(requireActivity(), "Category preferences failed. Try again")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            categoryViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarPer.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun setupChipGroup(optionList:MutableList<VideoCategory>){
        var idList = mutableListOf<Int>()
        for (chipData in optionList){
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

            mChip.setOnCheckedChangeListener { buttonView, isChecked ->
                isModified = true
                AppPreference.isModified = true

                if (isChecked) {
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.check)
                    mChip.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )

                    mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    selectedNumbers++
                    updateSelectedItem(chipData.id, chipData.name, true, "", isDefalultSelected = true)

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
                    selectedNumbers--
                    updateSelectedItem(chipData.id, chipData.name, false, "", isDefalultSelected = false)
                }
            }
        }
    }

    private fun updateSelectedItem(id: String, name: String, selected: Boolean, icon: String, isDefalultSelected:Boolean) {
        for (item in selectedItemList){
            if (item.id == id){
                item.selected = selected
                item.name = name
                item.icon = icon
                item.default_select = isDefalultSelected
            }
        }
    }

    private fun getSelectedCategoriesId(categoriesList: List<VideoCategory>){
        if (selectedCategoriesId.isNotEmpty()){
            selectedCategoriesId.clear()
        }
        for (item in categoriesList){
            if (item.default_select){
                selectedCategoriesId.add(item.id)
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
}