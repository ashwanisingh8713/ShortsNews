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
import com.ns.shortsnews.user.domain.models.LanguageData
import com.ns.shortsnews.user.domain.models.VideoCategory
import com.ns.shortsnews.user.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
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
    private val userViewModel: UserViewModel by activityViewModels { UserViewModelFactory().apply {
        inject(
            UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
            LanguageDataUseCase(UserDataRepositoryImpl(get())),
        )
    }}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguageBinding.bind(view)
        userViewModel.requestLanguagesApi()


        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarLanguages.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","OTPFragment onError ::: $it")
                    ShowToast.showGravityToast(requireActivity(), AppConstants.OTP_VALIDATION_ERROR)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.LanguagesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","OTPFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarLanguages.visibility = View.GONE
                    setupChipGroup(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarLanguages.visibility = View.VISIBLE
                }
            }
        }

        binding.continueButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("name", /*it.name*/"kamlesh")
            userViewModel.updateFragment(UserViewModel.MAIN_ACTIVITY,bundle )
        }
    }
    private fun setupChipGroup(optionList:List<LanguageData>){
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
                        mChip.background = ContextCompat.getDrawable(requireContext(), com.videopager.R.drawable.follow_background)
                        mChip.checkedIcon = ContextCompat.getDrawable(requireContext(), R.drawable.right)
                        mChip.setChipBackgroundColorResource(com.videopager.R.color.transparent_color)
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