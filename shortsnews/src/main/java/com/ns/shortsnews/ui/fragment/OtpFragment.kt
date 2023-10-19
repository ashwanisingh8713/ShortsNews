package com.ns.shortsnews.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentOtpBinding
import com.ns.shortsnews.data.mapper.UserOtp
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.domain.models.VideoCategory
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.notification.FCMTokenDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.domain.usecase.video_category.UpdateVideoCategoriesUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.NotificationViewModel
import com.ns.shortsnews.ui.viewmodel.NotificationViewModelFactory
import com.ns.shortsnews.ui.viewmodel.OTPViewModel
import com.ns.shortsnews.ui.viewmodel.OTPViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.*
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
import com.videopager.utils.UtilsFunctions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class OtpFragment : Fragment(R.layout.fragment_otp) {
    lateinit var binding: FragmentOtpBinding

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory().apply {
            inject(
                LanguageDataUseCase(UserDataRepositoryImpl(get())),
                UserSelectionsDataUseCase(UserDataRepositoryImpl(get()))
            )
        }
    }

    private val otpViewModel: OTPViewModel by activityViewModels { OTPViewModelFactory().apply {
        inject(
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())), UserSelectionsDataUseCase(UserDataRepositoryImpl(get()))
        )
    }}

    private val videoCategoryViewModel: VideoCategoryViewModel by activityViewModels {
        VideoCategoryViewModelFactory().apply {
            inject(
                VideoCategoryUseCase(VideoCategoryRepositoryImp(get())),
                UpdateVideoCategoriesUseCase(VideoCategoryRepositoryImp(get()))
            )
        }
    }

    private var isOTPVerified = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)
        val otpId: String? = arguments?.getString("otp_id")
        val emailId = arguments?.getString("email")
        val isUserRegistered = arguments?.getBoolean("isUserRegistered")


        // Device BackButton Click Listener
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isOTPVerified){
                // DO Nothing
            } else {
                activity?.finish()
            }
        }


        isUserRegistered.let {
            if (!isUserRegistered!!) {
                binding.nameConLayout.visibility = View.VISIBLE
            }
        }


        binding.emailTxt.text = emailId

        // Submit Button Click Listener
        binding.submitButton.setOnClickListener {
            UtilsFunctions.hideKeyBord(requireActivity(), view)
            val otpValue = binding.otpEditText.text.toString().trim()
            val name = binding.nameEditText.text.toString().trim()
            if (isUserRegistered == false){
                if (otpValue.isNotEmpty() && otpValue.length == 6 && name.isNotEmpty()) {
                    val data: MutableMap<String, String> = mutableMapOf()
                        data["OTP"] = otpValue
                        data["OTP_id"] = otpId.toString()
                        data["name"] = name

                    if (NetworkXProvider.isInternetConnected) {
                        otpViewModel.requestOtpValidationApi(data)
                    } else {
                        // No Internet Snack bar: Fire
                        NoConnection.noConnectionSnackBarInfinite(
                            binding.root,
                            requireContext() as AppCompatActivity
                        )
                    }
                } else {
                    if (otpValue.isEmpty() && name.isEmpty()) {
                        Alert().showGravityToast(requireActivity(), AppConstants.FILL_NAME)
                    } else if (name.isEmpty()) {
                        Alert().showGravityToast(requireActivity(), AppConstants.FILL_NAME)
                    } else if (otpValue.isEmpty()) {
                        Alert().showGravityToast(requireActivity(), AppConstants.FILL_OTP)
                    } else {
                        if (otpValue.length < 6) {
                            Alert().showGravityToast(requireActivity(), AppConstants.FILL_VALID_OTP)
                        }
                    }
                }
            } else {
                if (otpValue.isNotEmpty() && otpValue.length == 6) {
                    val data: MutableMap<String, String> = mutableMapOf()
                    data["OTP"] = otpValue
                    data["OTP_id"] = otpId.toString()
                    if (UtilsFunctions.isOnline(requireActivity())) {
                        // Making OTP Verification API Call
                        otpViewModel.requestOtpValidationApi(data)
                    } else {
                        // No Internet Snack bar: Fire
                        NoConnection.noConnectionSnackBarInfinite(
                            binding.root,
                            requireContext() as AppCompatActivity
                        )
                    }
                } else {
                    if (otpValue.isEmpty()) {
                        Alert().showGravityToast(requireActivity(), AppConstants.FILL_OTP)
                    } else {
                        if (otpValue.length < 6) {
                            Alert().showGravityToast(requireActivity(), AppConstants.FILL_VALID_OTP)
                        }
                    }
                }

            }
        }


        // Selections Error State Listener
        viewLifecycleOwner.lifecycleScope.launch {
            otpViewModel.userSelectionsErrorState.filterNotNull().collectLatest {
                Alert().showGravityToast(requireActivity(), it)
                binding.submitButton.visibility = View.VISIBLE
                binding.progressBarOtp.visibility = View.GONE
                isOTPVerified = false
            }
        }

        // OTP Error State Listener
        viewLifecycleOwner.lifecycleScope.launch {
            otpViewModel.errorState.filterNotNull().collectLatest {
                Alert().showGravityToast(requireActivity(), it)
                binding.submitButton.visibility = View.VISIBLE
                binding.progressBarOtp.visibility = View.GONE
                isOTPVerified = false
            }
        }


        // OTP Success State Listener
        viewLifecycleOwner.lifecycleScope.launch {
            otpViewModel.otpSuccessState.filterNotNull().collectLatest {
                it.let {
                    if (it.status) {
                        // Saving User Data in Preference
                        isOTPVerified = true
                        saveUserPreference(it)
                        Log.i("OTPSuccess", "OtpFragment :: otpSuccessState = ${it}")
                        if (it.first_time_user) {
                            binding.submitButton.visibility = View.VISIBLE
                            binding.progressBarOtp.visibility = View.GONE
                            userViewModel.updateFragment(UserViewModel.LANGUAGES, Bundle())
                        } else {
                            otpViewModel.requestUserSelectionApi()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            otpViewModel.userSelectionSuccessState.filterNotNull().collectLatest {
                    it.let {
                        val languageSet = it.languages.map { langid ->
                            langid.trim()
                        }
                        if (languageSet.isEmpty()) {
                            binding.submitButton.visibility = View.VISIBLE
                            binding.progressBarOtp.visibility = View.GONE
                            // When Language is empty, It should open Language Fragment
                            userViewModel.updateFragment(UserViewModel.LANGUAGES, Bundle())
                            Log.i("OTPSuccess", "OtpFragment :: userSelectionSuccessState IF")
                        } else {
                            AppPreference.saveSelectedLanguagesToPreference(languageSet)
                            videoCategoryViewModel.loadVideoCategory()
                            Log.i("OTPSuccess", "OtpFragment :: userSelectionSuccessState ELSE")
                        }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().filter {
                it.videoCategories.isNotEmpty()
            }.collectLatest {
                getSelectedVideoInterstCategory(it.videoCategories as MutableList<VideoCategory>)

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            otpViewModel.loadingState.collectLatest {
                if (it) {
                    binding.submitButton.visibility = View.GONE
                    binding.progressBarOtp.visibility = View.VISIBLE
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
        AppPreference.saveCategoriesToPreference(finalList)
        AppPreference.init(requireActivity())
        AppPreference.isRefreshRequired = false
        userViewModel.updateFragment(UserViewModel.MAIN_ACTIVITY, Bundle())
    }


    private fun saveUserPreference(it: UserOtp) {
        AppPreference.userName = it.name
        AppPreference.userEmail = it.email
        AppPreference.userToken = it.access_token
        AppPreference.isUserLoggedIn = true
        AppPreference.userProfilePic = it.userProfileImage
        AppPreference.userAge = it.age
        AppPreference.userLocation = it.location
    }
}