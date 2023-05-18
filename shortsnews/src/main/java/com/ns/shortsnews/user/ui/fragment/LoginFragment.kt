package com.ns.shortsnews.user.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.CoroutineWorker
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLoginBinding
import com.ns.shortsnews.databinding.NewLoginScreenLayoutBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.ShowToast
import com.ns.shortsnews.utils.Validation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private val userViewModel: UserViewModel by activityViewModels { UserViewModelFactory().apply {
        inject(
            UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
            LanguageDataUseCase(UserDataRepositoryImpl(get())),
        )
    }}

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        binding.sendImage.setOnClickListener{
            val email = binding.emailEditText.text.toString()
            if (email.isNotEmpty()) {
                if (Validation.validateEmail(email)){
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                    val bundle:MutableMap<String, String> = mutableMapOf()
                    bundle["email"] = email
                    userViewModel.requestRegistrationApi(bundle)
                } else {
                    ShowToast.showGravityToast(requireActivity(), AppConstants.FILL_VALID_EMAIL)
                }
            } else{
                ShowToast.showGravityToast(requireActivity(),AppConstants.FILL_REQUIRED_FIELD)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                binding.loginProgressBar.visibility = View.GONE
              if(it != "NA"){
                  Log.i("kamlesh","$it")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.registrationSuccessState.filterNotNull().collectLatest {
                it.let {
                    Log.i("kamlesh","Registration Response ::: $it")
                    binding.loginProgressBar.visibility = View.GONE
                    val bundle = Bundle()
                    bundle.putString("email", it.email)
                    bundle.putString("otp_id", it.OTP_id.toString())
                    userViewModel.updateFragment(UserViewModel.OTP,bundle )
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.filterNotNull().collectLatest {
                Log.i("kamlesh","data :: $it")
                if (it) {
                    binding.loginProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

}