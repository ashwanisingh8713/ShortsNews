package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.ns.shortsnews.R
import com.ns.shortsnews.data.mapper.UserVideoCategory
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.databinding.FragmentInterestsBinding
import com.ns.shortsnews.domain.models.InterestsTable
import com.ns.shortsnews.domain.models.VideoCategory
import com.ns.shortsnews.domain.repository.InterestsRepository
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class InterestsFragment : Fragment(R.layout.fragment_interests) {
    lateinit var binding: FragmentInterestsBinding
    private var interestsListDB = emptyList<InterestsTable>()
    private val interestsDao = ShortsDatabase.instance!!.interestsDao()
    private val interestsRepository = InterestsRepository(interestsDao)
    private val interestsViewModel: InterestsViewModel by activityViewModels { InterestsViewModelFactory(interestsRepository) }

    private val languageDao = ShortsDatabase.instance!!.languageDao()
    private val languageItemRepository = LanguageRepository(languageDao)
    private val languageViewModel: LanguageViewModel by activityViewModels { LanguageViewModelFactory(languageItemRepository) }

    private var selectedNumbers = 0
    private var countValue = 0
    private val categoryViewModel: VideoCategoryViewModel by activityViewModels { VideoCategoryViewModelFactory().apply {
        inject(VideoCategoryUseCase(VideoCategoryRepositoryImp(get())))
    } }

    private var selectedItemList = mutableListOf<VideoCategory>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInterestsBinding.bind(view)
        getSelectedLanguagesValues()

        binding.backButtonUser.setOnClickListener {
            activity?.finish()
        }
        binding.submitButtonConst.setOnClickListener {
            getSelectedVideoInterstCategory(selectedItemList)
            if (selectedItemList.isNotEmpty()){
                for (item in selectedItemList){
                    interestsViewModel.update(item.id, item.name, item.selected, "")
                }
                activity?.finish()
            } else {
                activity?.finish()
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
                    Log.i("kamlesh", "Registration Response ::: $it")
                    binding.progressBarPer.visibility = View.GONE
                    if (it.videoCategories.isNotEmpty()) {
                        countValue = it.videoCategories.size
                        if (interestsViewModel.isEmpty()) {
                            loadDataFromServer(it.videoCategories.toMutableList())
                            selectedItemList.addAll(it.videoCategories)
                        } else {
                            val finalList: MutableList<VideoCategory> = compareDBServer(it.videoCategories.toMutableList(), AppPreference.categoryList)
                            selectedItemList.addAll(finalList)
                            loadDataFromDB(finalList)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            categoryViewModel.loadingState.filterNotNull().collectLatest {
                Log.i("kamlesh","data :: $it")
                if (it) {
                    binding.progressBarPer.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            interestsViewModel.sharedDeleteFromTable.filterNotNull().collectLatest {
                if (it != null){
                    Log.i("database","Delete :: ${it.id}")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            interestsViewModel.sharedInsertInTable.filterNotNull().collectLatest {
                if (it != null){
                    Log.i("database","Inserted :: ${it.id}")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            interestsViewModel.getAllInterestedItems().filterNotNull().filter { it.isNotEmpty() }.collectLatest {
                Log.i("database", "All data :: $it")
                interestsListDB = it
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
            if (chipData.selected) {
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
                    updateSelectedItem(chipData.id, chipData.name, true, "")

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
                    updateSelectedItem(chipData.id, chipData.name, false, "")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateSelectedItems(){
        binding.selectedTxt.text = "Selected $selectedNumbers/$countValue categories"

    }

    private fun updateSelectedItem(id: String, name: String, selected: Boolean, icon: String) {
        for (item in selectedItemList){
            if (item.id == id){
                item.selected = selected
                item.name = name
                item.icon = icon
            }
        }
    }


    private fun loadDataFromDB( list: MutableList<VideoCategory>){
        setupChipGroup(list)
    }

    private fun loadDataFromServer(list: MutableList<VideoCategory>) {
        for (data in list){
            interestsViewModel.insert(data.id,data.name,false,"")
        }
        setupChipGroup(list)
    }

    private fun compareDBServer(interestsList: MutableList<VideoCategory>,
                                interestsTableList:MutableList<VideoCategory> ):MutableList<VideoCategory>{
        var finalList:MutableList<VideoCategory> = mutableListOf()
        for (interestsData in interestsList){
            var matched:Boolean = false
            for (interestsTable in interestsTableList){
                if (interestsData.id == interestsTable.id){
                    var convertedData = VideoCategory(interestsTable.id,interestsTable.name,interestsTable.selected,interestsTable.icon)
                    finalList.add(convertedData)
                    matched = true
                }
            }
            if (matched){
                matched = false
            } else {
                interestsViewModel.delete(interestsData.id, interestsData.name, false, "")
            }
        }
        return finalList
    }

    private fun getSelectedLanguagesValues() {
        var languageString = ""
        lifecycleScope.launch {
            languageViewModel.getAllLanguage().filterNotNull().filter { it.isNotEmpty() }.collectLatest {
                for (data in it){
                    if (data.selected){
                        languageString = languageString + data.id +","
                    }
                }
                categoryViewModel.loadVideoCategory(languageString)
            }
        }
    }

    fun getSelectedVideoInterstCategory(categoryList:MutableList<VideoCategory>){
        val unselectedCategory = mutableListOf<VideoCategory>()
        val selectedCategory = mutableListOf<VideoCategory>()

        for (item in categoryList){
            if (item.selected){
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