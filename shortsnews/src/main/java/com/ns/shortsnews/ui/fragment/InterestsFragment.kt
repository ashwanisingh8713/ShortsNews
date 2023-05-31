package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ns.shortsnews.R
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.databinding.FragmentInterestsBinding
import com.ns.shortsnews.domain.models.VideoCategory
import com.ns.shortsnews.domain.repository.InterestsRepository
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class InterestsFragment : Fragment(R.layout.fragment_interests) {
    lateinit var binding: FragmentInterestsBinding
    private val interestsDao = ShortsDatabase.instance!!.interestsDao()
    private val interestsRepository = InterestsRepository(interestsDao)
    private val interestsViewModel: InterestsViewModel by activityViewModels { InterestsViewModelFactory(interestsRepository) }

    var selectedNumbers = 0
    var countValue = 0
    private val categoryViewModel: VideoCategoryViewModel by activityViewModels { VideoCategoryViewModelFactory().apply {
        inject(VideoCategoryUseCase(VideoCategoryRepositoryImp(get())))
    } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInterestsBinding.bind(view)

        binding.backButtonUser.setOnClickListener {
            activity?.finish()
        }
        binding.submitButtonPers.setOnClickListener {
            requireActivity().finish()
        }

        categoryViewModel.loadVideoCategory()
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
                    Log.i("kamlesh","Registration Response ::: $it")
                    binding.progressBarPer.visibility = View.GONE
                    countValue = it.videoCategories.size
                    setupChipGroup(it.videoCategories as MutableList<VideoCategory>)
                    updateSelectedItems(selectedNumbers,countValue)
                    for (data in it.videoCategories){
                        interestsViewModel.insert(data.id, data.name,false, "")
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
            binding.choiceChipGroup.addView(mChip)
            binding.choiceChipGroup.isClickable = true
            binding.choiceChipGroup.isSingleSelection = false
            mChip.isChipIconVisible = true
            mChip.chipStrokeWidth = 4F
            mChip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck)

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
                    updateSelectedItems(selectedNumbers, countValue)
                    interestsViewModel.insert(chipData.id, chipData.name, true, "")
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
                    updateSelectedItems(selectedNumbers, countValue)
                    interestsViewModel.insert(chipData.id, chipData.name, true, "")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateSelectedItems(selectedCount:Int, totalCount:Int){
        binding.selectedTxt.text = "Selected $selectedCount/$totalCount languages"

    }
}