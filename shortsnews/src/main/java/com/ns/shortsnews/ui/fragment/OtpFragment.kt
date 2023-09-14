package com.ns.shortsnews.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentOtpBinding
import com.ns.shortsnews.data.mapper.UserOtp
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.notification.FCMTokenDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.ui.viewmodel.NotificationViewModel
import com.ns.shortsnews.ui.viewmodel.NotificationViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.*
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
import com.videopager.utils.UtilsFunctions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class OtpFragment : Fragment(R.layout.fragment_otp) {
    lateinit var binding: FragmentOtpBinding

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory().apply {
            inject(
                UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
                UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
                LanguageDataUseCase(UserDataRepositoryImpl(get())),
            )
        }
    }
    private val notificationViewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory().apply {
            inject(FCMTokenDataUseCase(UserDataRepositoryImpl(get())))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)
        val otpId: String? = arguments?.getString("otp_id")
        val emailId = arguments?.getString("email")
        val isUserRegistered = arguments?.getBoolean("isUserRegistered")
        isUserRegistered.let {
            if (!isUserRegistered!!) {
                binding.nameConLayout.visibility = View.VISIBLE
            }
        }
        binding.emailTxt.text = emailId
        binding.submitButton.setOnClickListener {
            UtilsFunctions.hideKeyBord(requireActivity(), view)
            val otpValue = binding.otpEditText.text.toString().trim()
            val name = binding.nameEditText.text.toString().trim()
            if (otpValue.isNotEmpty() && otpValue.length == 6 && name.isNotEmpty()) {
                val data: MutableMap<String, String> = mutableMapOf()
                data["OTP"] = otpValue
                data["OTP_id"] = otpId.toString()
                if (NetworkXProvider.isInternetConnected) {
                    userViewModel.requestOtpValidationApi(data)
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
                } else if (otpValue.isNotEmpty() || name.isEmpty()) {
                    Alert().showGravityToast(requireActivity(), AppConstants.FILL_NAME)
                } else if (otpValue.isEmpty() || name.isNotEmpty()) {
                    Alert().showGravityToast(requireActivity(), AppConstants.FILL_OTP)
                } else {
                    if (otpValue.length < 6) {
                        Alert().showGravityToast(requireActivity(), AppConstants.FILL_VALID_OTP)
                    }
                }
            }

        }


        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarOtp.visibility = View.GONE
                binding.submitButton.visibility = View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.otpSuccessState.filterNotNull().collectLatest {
                it.let {
                    sendFcmTokenToServer()
                    saveUserPreference(it)
                    delay(500)
                    binding.progressBarOtp.visibility = View.GONE
                    val bundle = Bundle()
                    bundle.putBoolean("first_time_user", it.first_time_user)
                    userViewModel.updateFragment(UserViewModel.LANGUAGES, bundle)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            userViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.submitButton.visibility = View.GONE
                    binding.progressBarOtp.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun sendFcmTokenToServer() {
        val bundle: MutableMap<String, String> = mutableMapOf()
        bundle["platform"] = "Android"
        bundle["device_token"] = AppPreference.fcmToken.toString()
        notificationViewModel.requestSendFcmToken(bundle)

        lifecycleScope.launch {
            notificationViewModel.SendNotificationSuccessState.filterNotNull().collectLatest {
                if (it.status) {
                    Log.i("Token", "Token Send ")
                }
            }
        }

        lifecycleScope.launch {
            notificationViewModel.errorState.filterNotNull().collectLatest {
                Log.i("Token", "Error in sending token $it")
            }
        }
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