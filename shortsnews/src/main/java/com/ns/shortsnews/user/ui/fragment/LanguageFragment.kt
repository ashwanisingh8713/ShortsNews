package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLanguageBinding
import com.ns.shortsnews.databinding.FragmentPersonaliseBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.user.domain.models.VideoCategory
import com.ns.shortsnews.user.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.user.ui.viewmodel.*
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.ShowToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LanguageFragment : Fragment(R.layout.fragment_language) {
    lateinit var binding:FragmentLanguageBinding
    private val languagesDataViewModel: LanguagesDataViewModel by activityViewModels { LanguagesViewModelFactory().apply {
        inject(LanguageDataUseCase(UserDataRepositoryImpl(get())))
    } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguageBinding.bind(view)
        languagesDataViewModel.requestLanguagesApi()


        viewLifecycleOwner.lifecycleScope.launch(){
            languagesDataViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarLanguages.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","OTPFragment onError ::: $it")
                    ShowToast.showGravityToast(requireActivity(), AppConstants.OTP_VALIDATION_ERROR)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            languagesDataViewModel.LanguagesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","OTPFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarLanguages.visibility = View.GONE
                    val bundle = Bundle()
                    bundle.putString("name", /*it.name*/"kamlesh")
//                    languagesDataViewModel.updateFragment(UserViewModel.MAIN_ACTIVITY,bundle )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            languagesDataViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarLanguages.visibility = View.VISIBLE
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

            mChip.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked){
                        mChip.isCheckedIconVisible = true
                        mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background_chip)
                        mChip.checkedIcon = ContextCompat.getDrawable(requireContext(), R.drawable.right)
                        mChip.setChipBackgroundColorResource(R.color.button_background)
                        mChip.isChipIconVisible = false
//                        selectedNumbers++
//                        updateSelectedItems(selectedNumbers,countValue)

                    } else{
                        mChip.isChipIconVisible = true
                        mChip.background = ContextCompat.getDrawable(requireContext(), R.drawable.roud_corner_background)
                        mChip.setChipBackgroundColorResource(R.color.screen_background)
                        mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.add)
//                        selectedNumbers--
//                        updateSelectedItems(selectedNumbers,countValue)
                    }
                }
            })
        }
    }
}