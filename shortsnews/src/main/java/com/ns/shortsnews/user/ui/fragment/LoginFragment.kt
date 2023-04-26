package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.ProfileActivity
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLoginBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.Validation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private val userViewModel: UserViewModel by activityViewModels { UserViewModelFactory().apply {
        inject(
            UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
        )
    }}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)


        binding.submitButton.setOnClickListener{
            val name = binding.nameTextInput.text.toString()
            val email = binding.emailTextInput.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty()) {
                if (Validation.validateEmail(email)){
                    val bundle:MutableMap<String, String> = mutableMapOf()
                    bundle["email"] = email
                    bundle["name"] = name
                    userViewModel.requestRegistrationApi(bundle)
                } else {
                    Toast.makeText(requireActivity(),"Please enter valid Email id",Toast.LENGTH_LONG).show()
                }
            } else{
                Toast.makeText(requireActivity(),"Please fill all required field",Toast.LENGTH_LONG).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.collectLatest {
              if(!it.equals("NA")){
                Toast.makeText(requireActivity(),"$it",Toast.LENGTH_LONG).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.registrationSuccessState.collectLatest {
                it.let {
                    binding.progressBarLogin.visibility = View.GONE
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.collectLatest {
              binding.progressBarLogin.visibility = View.VISIBLE
            }
        }


    }

}