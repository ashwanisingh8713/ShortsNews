package com.ns.shortsnews.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentOtpBinding
import com.ns.shortsnews.data.mapper.UserOtp
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class OtpFragment : Fragment(R.layout.fragment_otp) {
    lateinit var binding: FragmentOtpBinding

    private val userViewModel: UserViewModel by activityViewModels  { UserViewModelFactory().apply {
        inject(
            UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
            LanguageDataUseCase(UserDataRepositoryImpl(get())),
        )
    }}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)
        val  otpId:String? = arguments?.getString("otp_id")
        val emailId = arguments?.getString("email")
        binding.emailTxt.text = emailId
//        binding.backButtonOtp.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("name", "")
//            userViewModel.updateFragment(UserViewModel.OTP_POP,bundle )
//        }
        binding.otpEditText.setText("123456")
        binding.submitButton.setOnClickListener {

             val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            val otpValue = binding.otpEditText.text.toString()
            if (otpValue.isNotEmpty() && otpValue.length == 6){
                val data:MutableMap<String, String> = mutableMapOf()
                data["OTP"] = otpValue
                data["OTP_id"] = otpId.toString()
                data["name"] = "kamlesh"
                if (Alert().isOnline(requireActivity())) {
                    userViewModel.requestOtpValidationApi(data)
                } else {
                    Alert().showErrorDialog(
                        AppConstants.CONNECTIVITY_ERROR_TITLE,
                        AppConstants.CONNECTIVITY_MSG,
                        requireActivity()
                    )
                }
            } else{
                if (otpValue.isEmpty()){
                    Alert().showGravityToast(requireActivity(),AppConstants.FILL_OTP)
                } else {
                    Alert().showGravityToast(requireActivity(), AppConstants.FILL_VALID_OTP)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarOtp.visibility = View.GONE
                if(it != "NA"){
                    Log.i("kamlesh","OTPFragment onError ::: $it")
                    binding.submitButton.visibility = View.VISIBLE
                    Alert().showGravityToast(requireActivity(), AppConstants.OTP_VALIDATION_ERROR)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.otpSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh","OTPFragment onSuccess ::: $it")
                it.let {
                    saveUserPreference(it)
                    binding.progressBarOtp.visibility = View.GONE
                    val bundle = Bundle()
                    bundle.putString("name", /*it.name*/"kamlesh")
                    userViewModel.updateFragment(UserViewModel.MAIN_ACTIVITY,bundle )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.submitButton.visibility = View.GONE
                    binding.progressBarOtp.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun saveUserPreference(it: UserOtp){
        AppPreference.userName = it.name
        AppPreference.userEmail = it.email
        AppPreference.userToken = it.access_token
        AppPreference.isUserLoggedIn = true
        AppPreference.userProfilePic = it.userProfileImage
        AppPreference.userAge = it.age
        AppPreference.userLocation = it.location
    }
}