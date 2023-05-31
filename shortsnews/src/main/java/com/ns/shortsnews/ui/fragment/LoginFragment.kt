package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.CoroutineWorker
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLoginBinding
import com.ns.shortsnews.databinding.NewLoginScreenLayoutBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.connectivity.ConnectionStatus
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
            val name = binding.nameEditText.text.toString()
            if (email.isNotEmpty()  && name.trim().isNotEmpty()) {
                if (validateEmail(email)){
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                    val bundle:MutableMap<String, String> = mutableMapOf()
                    bundle["email"] = email
                    bundle["name"] = name
                    if (Alert().isOnline(requireActivity())) {
                        userViewModel.requestRegistrationApi(bundle)
                    } else {
                            Alert().showErrorDialog(AppConstants.CONNECTIVITY_ERROR_TITLE,AppConstants.CONNECTIVITY_MSG, requireActivity())
                    }
                } else {
                    Alert().showGravityToast(requireActivity(), AppConstants.FILL_VALID_EMAIL)
                }
            } else{
                if (name.isEmpty()){
                    Alert().showGravityToast(requireActivity(),AppConstants.FILL_NAME_REQUIRED_FIELD)
                }
                if (email.isEmpty()){
                    Alert().showGravityToast(requireActivity(),AppConstants.FILL_EMAIL_REQUIRED_FIELD)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
              if(it != "NA"){
                  binding.progressBarLogin.visibility = View.GONE
                  binding.sendImage.visibility = View.VISIBLE
                      Alert().showErrorDialog(AppConstants.API_ERROR_TITLE, it, requireActivity())
                  Log.i("kamlesh","$it")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.registrationSuccessState.filterNotNull().collectLatest {
                it.let {
                    Log.i("kamlesh","Registration Response ::: $it")
                    binding.progressBarLogin.visibility = View.GONE
                    binding.sendImage.visibility = View.VISIBLE
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
                    binding.sendImage.visibility = View.GONE
                    binding.progressBarLogin.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validateEmail(email:String):Boolean{
        return if (email.isEmpty()){
            false
        } else Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}