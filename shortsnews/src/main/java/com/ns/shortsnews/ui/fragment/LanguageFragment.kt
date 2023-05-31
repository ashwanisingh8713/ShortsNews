package com.ns.shortsnews.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLanguageBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.LanguageTable
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.ui.viewmodel.LanguageViewModel
import com.ns.shortsnews.ui.viewmodel.LanguageViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LanguageFragment : Fragment(R.layout.fragment_language) {
    lateinit var binding:FragmentLanguageBinding
    private val languageDao = ShortsDatabase.instance!!.languageDao()
    private val languageItemRepository = LanguageRepository(languageDao)

    private val userViewModel: UserViewModel by activityViewModels { UserViewModelFactory().apply {
        inject(
            UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
            LanguageDataUseCase(UserDataRepositoryImpl(get())),
        )
    }}
    private val languageViewModel:LanguageViewModel by activityViewModels { LanguageViewModelFactory(languageItemRepository)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguageBinding.bind(view)
        userViewModel.requestLanguagesApi()


        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarLanguages.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","OTPFragment onError ::: $it")
                    Alert().showGravityToast(requireActivity(), AppConstants.OTP_VALIDATION_ERROR)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.LanguagesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","OTPFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarLanguages.visibility = View.GONE
                    for (data in it){
                        languageViewModel.insert(data.id,data.name,data.slug,false,"")
                    }
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
        viewLifecycleOwner.lifecycleScope.launch {
            languageViewModel.sharedDeleteFromTable.filterNotNull().collectLatest {
                if (it != null){
                    Log.i("database","Delete :: ${it.id}")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            languageViewModel.sharedInsertInTable.filterNotNull().collectLatest {
                if (it != null){
                    Log.i("database","Inserted :: ${it.id}")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            languageViewModel.getAllLanguage().filterNotNull().filter { it.isNotEmpty() }.collectLatest {
                Log.i("database", "All data :: $it")
            }
        }

        binding.submitButtonConst.setOnClickListener {
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
            mChip.chipStrokeWidth = 4F
            mChip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck)


            mChip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.check)
                    mChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                    mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    languageViewModel.update(chipData.id, chipData.name, chipData.slug,true, "")
                } else {
                    mChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.videopager.R.color.black))
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck)
                    mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    mChip.chipStrokeWidth = 4F
                    mChip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                    languageViewModel.update(chipData.id, chipData.name,chipData.slug ,false,"")
                }
            }
        }
    }
}