package com.ns.shortsnews.user.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentPersonaliseBinding
import com.ns.shortsnews.user.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.user.domain.models.VideoCategory
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class PersonaliseFragment : Fragment(com.ns.shortsnews.R.layout.fragment_personalise) {
    lateinit var binding: FragmentPersonaliseBinding
    private val categoryViewModel: VideoCategoryViewModel by activityViewModels { VideoCategoryViewModelFactory().apply {
        inject(VideoCategoryUseCase(VideoCategoryRepositoryImp(get())))
    } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPersonaliseBinding.bind(view)
        binding.backButtonUser.setOnClickListener {
            activity?.finish()
        }
        binding.choiceChipGroup.setOnCheckedStateChangeListener(object : ChipGroup.OnCheckedStateChangeListener{
            override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {

            }

        })
        categoryViewModel.loadVideoCategory()
        viewLifecycleOwner.lifecycleScope.launch(){
            categoryViewModel.errorState.filterNotNull().collectLatest {
                if(it != "NA"){
                    Toast.makeText(requireActivity(),"$it",Toast.LENGTH_LONG).show()
                    binding.progressBarPer.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            categoryViewModel.videoCategorySuccessState.filterNotNull().collectLatest {
                it.let {
                    Log.i("kamlesh","Registration Response ::: $it")
                    binding.progressBarPer.visibility = View.GONE
                    setupChipGroup(it.videoCategories as MutableList<VideoCategory>)
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
            var mChip =
                this.layoutInflater.inflate(R.layout.item_chip_view, null, false) as Chip
            mChip.text = "+ ${chipData.name}"
            mChip.isCheckable = true
            mChip.isClickable = true
            binding.choiceChipGroup.addView(mChip)
            binding.choiceChipGroup.isClickable = true
            binding.choiceChipGroup.isSingleSelection = false
            mChip.setOnClickListener {
                idList.add(mChip.id)
                Log.i("","${mChip.id}")
                if (idList.contains(mChip.id)){
                    idList.remove(mChip.id)
//                    mChip.isCheckedIconVisible = true
                    mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background_chip)
                    mChip.setChipBackgroundColorResource(R.color.button_background);
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.right)
                } else{
                    idList.add(mChip.id)
                    mChip.isChipIconVisible = false
                    mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background_chip)


                }

            }


//            mChip.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
//                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//                    if (isChecked){
//                        mChip.chipIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.right)
//                        mChip.isChipIconVisible = true
//                    } else{
//                        mChip.isChipIconVisible = false
//                    }
//                }
//
//            })
        }
    }
}