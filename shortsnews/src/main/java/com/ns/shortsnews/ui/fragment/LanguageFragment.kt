package com.ns.shortsnews.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.ns.shortsnews.R
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.databinding.FragmentLanguageBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.database.ShortsDatabase
import com.ns.shortsnews.domain.repository.LanguageRepository
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.LanguageTable
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.LanguageViewModel
import com.ns.shortsnews.ui.viewmodel.LanguageViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.Alert
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.utils.CommonFunctions
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LanguageFragment : Fragment(R.layout.fragment_language) {
    lateinit var binding:FragmentLanguageBinding
    private var languageListDB = emptyList<LanguageTable>()
    private val languageItemRepository = LanguageRepository(ShortsDatabase.instance!!.languageDao())
    private var from:String = ""
    private var selectedLanguages = ""
    private val videoCategoryViewModel: VideoCategoryViewModel by activityViewModels {
        VideoCategoryViewModelFactory().apply {
            inject(
                VideoCategoryUseCase(VideoCategoryRepositoryImp(get()))
            )
        }
    }
    private val userViewModel: UserViewModel by activityViewModels { UserViewModelFactory().apply {
        inject(
            UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
            LanguageDataUseCase(UserDataRepositoryImpl(get())),
        )
    }}
    private val languageViewModel:LanguageViewModel by activityViewModels { LanguageViewModelFactory(languageItemRepository)}
    private var selectedNumbers = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguageBinding.bind(view)
        if (NetworkXProvider.isInternetConnected) {
            userViewModel.requestLanguagesApi()
        } else {
            NoConnection.noConnectionSnackBarInfinite(binding.root,
                requireContext() as AppCompatActivity
            )
        }
        from = arguments?.getString("from").toString()
        Log.i("launchFrom", "Language fragment from $from")
        if(AppPreference.isLanguageSelected){
            binding.continueButton.text = "Save"
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarLanguages.visibility = View.GONE
               Log.i("kamlesh","OTPFragment onError ::: $it")

            }
        }


        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.LanguagesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","OTPFragment onSuccess ::: $it")
                it.let {
                    binding.progressBarLanguages.visibility = View.GONE
                    if (it.isNotEmpty()) {
                        if (languageViewModel.isEmpty()) {
                            loadDataFromServer(it)
                        } else {
                            val finalList: List<LanguageData> = compareDBServer(it, languageListDB)
                            loadDataFromDB(finalList)
                        }
                    }
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
                Log.i("database","Delete :: ${it.id}")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            languageViewModel.sharedInsertInTable.filterNotNull().collectLatest {
                Log.i("database","Inserted :: ${it.id}")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            languageViewModel.getAllLanguage().filterNotNull().filter { it.isNotEmpty() }.collectLatest {
                Log.i("database", "All data :: $it")
                languageListDB = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().filter {
                it.videoCategories.isNotEmpty()
            }.collectLatest {
              // Save category data in preference
                AppPreference.saveCategoriesToPreference(categoryList = it.videoCategories)
                val bundle = Bundle()
                bundle.putString("name", /*it.name*/"")
                userViewModel.updateFragment(UserViewModel.MAIN_ACTIVITY, bundle)
            }
        }


        binding.submitButtonConst.setOnClickListener {
            if (selectedNumbers >0) {
                if (from == AppConstants.FROM_PROFILE) {
                    AppPreference.isLanguageSelected = true
                    AppPreference.selectedLanguages = selectedLanguages.dropLast(1)
                    videoCategoryViewModel.loadVideoCategory(selectedLanguages)
                } else {
                    activity?.finish()
                }
            } else {
                Alert().showGravityToast(requireActivity(), "Please select one language")
            }
        }
    }
    private fun setupChipGroup(optionList:List<LanguageData>){
        val idList = mutableListOf<Int>()
        for (chipData in optionList){
            idList.clear()
            val mChip =
                this.layoutInflater.inflate(R.layout.item_chip_view, null, false) as Chip
            mChip.text = chipData.name
            mChip.isCheckable = true
            mChip.isClickable = true
            mChip.iconStartPadding = 6F
            mChip.isChipIconVisible = true
            mChip.chipStrokeWidth = 4F
            mChip.checkedIcon =  ContextCompat.getDrawable(requireContext(), R.drawable.check)
            binding.choiceChipGroup.isSingleSelection = false
            binding.choiceChipGroup.isClickable = true
            binding.choiceChipGroup.addView(mChip)

            if (chipData.isSelected) {
                mChip.chipIcon =  ContextCompat.getDrawable(requireContext(), R.drawable.check)
                mChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                mChip.setChipBackgroundColorResource(R.color.white)
                mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                languageViewModel.update(chipData.id, chipData.name, chipData.slug,true, "")
                mChip.isChecked = true
                selectedNumbers++
            } else {
                mChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.videopager.R.color.black))
                mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck)
                mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                mChip.isChecked = false
                mChip.chipStrokeWidth = 4F
                mChip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                languageViewModel.update(chipData.id, chipData.name,chipData.slug ,false,"")
            }

            mChip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.check)
                    mChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                    mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    languageViewModel.update(chipData.id, chipData.name, chipData.slug,true, "")
                    createSelectedLanguagesValue(chipData.id)
                    selectedNumbers++
                } else {
                    mChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), com.videopager.R.color.black))
                    mChip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.uncheck)
                    mChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    mChip.chipStrokeWidth = 4F
                    mChip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                    languageViewModel.update(chipData.id, chipData.name,chipData.slug ,false,"")
                    createSelectedLanguagesValue(chipData.id)
                    selectedNumbers--
                }
            }
        }
    }

    private fun loadDataFromDB( list: List<LanguageData>){
        setupChipGroup(list)
    }

    private fun loadDataFromServer(list: List<LanguageData>) {
        for (data in list){
            languageViewModel.insert(data.id,data.name,data.slug,false,"")
        }
        setupChipGroup(list)
    }

    private fun compareDBServer(languageList: List<LanguageData>, languageTableList:List<LanguageTable> ):List<LanguageData>{
        var finalList:MutableList<LanguageData> = mutableListOf()
        for (languageData in languageList){
            var matched = false
            for (languageTable in languageTableList){
                if (languageData.id == languageTable.id){
                    var convertedData = LanguageData(languageTable.id,languageTable.name,languageTable.slug,languageTable.selected,languageTable.icon)
                    finalList.add(convertedData)
                    matched = true
                }
            }
            if (matched){
                matched = false
            } else {
                languageViewModel.delete(languageData.id, languageData.name, languageData.slug, false, "")
            }
        }
        return finalList
    }

    private fun createSelectedLanguagesValue(id:String){
        val tempValue = "$id,"
        var modifiedString = ""
        if (selectedLanguages.contains(tempValue)){
            modifiedString = selectedLanguages.replace(tempValue,"")
            selectedLanguages = modifiedString
        } else {
            selectedLanguages += tempValue
        }
    }
}