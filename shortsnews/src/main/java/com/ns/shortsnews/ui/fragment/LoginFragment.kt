package com.ns.shortsnews.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLoginBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.*
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
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
                if (email.isNotEmpty() && name.trim().isNotEmpty()) {
                    if (validateEmail(email)) {
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(view.windowToken, 0)
                        val bundle: MutableMap<String, String> = mutableMapOf()
                        bundle["email"] = email
                        bundle["name"] = name
                         if (NetworkXProvider.isInternetConnected) {
                            userViewModel.requestRegistrationApi(bundle)
                        } else {
                             // No Internet Snackbar: Fire
                             NoConnection.noConnectionSnackBarInfinite(binding.root,
                                 requireContext() as AppCompatActivity
                             )
                        }
                    } else {
                        Alert().showGravityToast(requireActivity(), AppConstants.FILL_VALID_EMAIL)
                    }
                } else {
                    if (name.isEmpty() || email.isEmpty()) {
                        Alert().showGravityToast(
                            requireActivity(),
                            AppConstants.FILL_REQUIRED_FIELD
                        )
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                  binding.progressBarLogin.visibility = View.GONE
                  binding.sendImage.visibility = View.VISIBLE
                      Alert().showErrorDialog(AppConstants.API_ERROR_TITLE, AppConstants.API_ERROR, requireActivity())
                  Log.i("kamlesh","$it")

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