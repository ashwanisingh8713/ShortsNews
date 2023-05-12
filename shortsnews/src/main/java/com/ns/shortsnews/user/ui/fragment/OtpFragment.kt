package com.ns.shortsnews.user.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentOtpBinding
import com.ns.shortsnews.user.data.mapper.UserOtp
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.PrefUtils
import com.ns.shortsnews.utils.Validation
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
        )
    }}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)
        val  otpId:String? = arguments?.getString("otp_id")
        val emailId = arguments?.getString("email")
        binding.emailOtpTxt.text = emailId
        binding.backButtonOtp.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("name", "")
            userViewModel.updateFragment(UserViewModel.OTP_POP,bundle )
        }

        binding.loginButton.setOnClickListener {
             val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            val otpValue = binding.otpTextInput.text.toString()
            if (otpValue.isNotEmpty() && otpValue.length == 6){
                val data:MutableMap<String, String> = mutableMapOf()
                data["OTP"] = otpValue
                data["OTP_id"] = otpId.toString()
                userViewModel.requestOtpValidationApi(data)
            } else{
                if (otpValue.isEmpty()){
                    val toast =   Toast.makeText(requireActivity(),"Please enter OTP",Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM, 0, 0)
                    toast.show()

                } else {
                    val toast =   Toast.makeText(requireActivity(),"Please enter valid OTP",Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM, 0, 0)
                    toast.show()
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.progressBarOtp.visibility = View.GONE
                if(!it.equals("NA")){
                    Log.i("kamlesh","OTPFragment onError ::: $it")
                    Toast.makeText(requireActivity(),"$it", Toast.LENGTH_LONG).show()
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
                    bundle.putString("name", it.name)
                    userViewModel.updateFragment(UserViewModel.PROFILE,bundle )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.progressBarOtp.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun saveUserPreference(it: UserOtp){
        PrefUtils.with(requireContext()).save(Validation.PREF_USERNAME, it.name)
        PrefUtils.with(requireContext()).save(Validation.PREF_USER_EMAIL, it.email)
        PrefUtils.with(requireContext()).save(Validation.PREF_USER_TOKEN, it.access_token)
        PrefUtils.with(requireContext()).save(Validation.PREF_IS_USER_LOGGED_IN, true)
    }
}