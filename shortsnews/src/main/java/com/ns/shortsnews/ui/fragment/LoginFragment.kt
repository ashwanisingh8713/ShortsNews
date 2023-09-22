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
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.*
import com.rommansabbir.networkx.NetworkXProvider
import com.videopager.utils.NoConnection
import com.videopager.utils.UtilsFunctions
import com.videopager.utils.UtilsFunctions.hideKeyBord
import com.videopager.utils.UtilsFunctions.isOnline
import com.videopager.utils.UtilsFunctions.validateEmail
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
            UserSelectionsDataUseCase(UserDataRepositoryImpl(get()))
        )
    }}

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        binding.sendImage.setOnClickListener{
                val email = binding.emailEditText.text.toString()
                if (email.isNotEmpty()) {
                    if (validateEmail(email)) {
                        hideKeyBord(requireActivity(), view)
                        val bundle: MutableMap<String, String> = mutableMapOf()
                        bundle["email"] = email
                         if (isOnline(requireActivity())) {
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
                    if (email.isEmpty()) {
                        Alert().showGravityToast(
                            requireActivity(),
                            AppConstants.FILL_EMAIL
                        )
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
                  binding.progressBarLogin.visibility = View.GONE
                  binding.sendImage.visibility = View.VISIBLE
                      Alert().showErrorDialog(AppConstants.API_ERROR_TITLE, AppConstants.API_ERROR, requireActivity())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.registrationSuccessState.filterNotNull().collectLatest {
                it.let {
                    val bundle = Bundle()
                    bundle.putString("email", it.email)
                    bundle.putString("otp_id", it.OTP_id.toString())
                    bundle.putBoolean("isUserRegistered",it.is_registered)
                    userViewModel.updateFragment(UserViewModel.OTP,bundle )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.filterNotNull().collectLatest {
                if (it) {
                    binding.sendImage.visibility = View.GONE
                    binding.progressBarLogin.visibility = View.VISIBLE
                } else {
                    binding.sendImage.visibility = View.VISIBLE
                    binding.progressBarLogin.visibility = View.GONE
                }
            }
        }
    }
}