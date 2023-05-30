package com.ns.shortsnews.ui.fragment

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
import com.ns.shortsnews.databinding.FragmentPersonaliseBinding
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.domain.models.VideoCategory
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class PersonaliseFragment : Fragment(com.ns.shortsnews.R.layout.fragment_personalise) {
    lateinit var binding: FragmentPersonaliseBinding
    var selectedNumbers = 0
    var countValue = 0
    private val categoryViewModel: VideoCategoryViewModel by activityViewModels { VideoCategoryViewModelFactory().apply {
        inject(VideoCategoryUseCase(VideoCategoryRepositoryImp(get())))
    } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPersonaliseBinding.bind(view)
        binding.backButtonUser.setOnClickListener {
            activity?.finish()
        }
        binding.submitButtonPers.setOnClickListener {
            Alert().showGravityToast(requireActivity(), AppConstants.SPRINT_THREE)
        }
        binding.choiceChipGroup.setOnCheckedStateChangeListener(object : ChipGroup.OnCheckedStateChangeListener{
            override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {

            }

        })
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
    }

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
            mChip.setChipBackgroundColorResource(R.color.screen_background)
            mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.add)

            mChip.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked){
                        mChip.isCheckedIconVisible = true
                        mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background_chip)
                        mChip.checkedIcon = ContextCompat.getDrawable(requireContext(), R.drawable.right)
                        mChip.setChipBackgroundColorResource(R.color.button_background)
                        mChip.isChipIconVisible = false
                        selectedNumbers++
                        updateSelectedItems(selectedNumbers,countValue)

                    } else{
                        mChip.isChipIconVisible = true
                        mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background)
                        mChip.setChipBackgroundColorResource(R.color.screen_background)
                        mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.add)
                        selectedNumbers--
                        updateSelectedItems(selectedNumbers,countValue)
                    }
                }
            })
        }
    }

    fun updateSelectedItems(selectedCount:Int, totalCount:Int){
        binding.selectedTxt.text = "Selected $selectedCount/$totalCount languages"

    }
}