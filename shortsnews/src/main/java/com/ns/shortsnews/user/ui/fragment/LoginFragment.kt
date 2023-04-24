package com.ns.shortsnews.user.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.ProfileActivity
import com.ns.shortsnews.R
import com.ns.shortsnews.databinding.FragmentLoginBinding
import com.ns.shortsnews.user.data.network.NetService
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCases
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCases
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCases
import com.ns.shortsnews.user.ui.viewmodel.TestViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.Validation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private val userViewModel: UserViewModel by activityViewModels { ProfileActivity.userViewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)


        binding.submitButton.setOnClickListener{
            if (binding.nameTextInput.text.isNotEmpty() && Validation.validateEmail(binding.emailTextInput.text.toString())) {
                val bundle:MutableMap<String, String> = mutableMapOf()
                bundle["email"] = binding.emailTextInput.toString()
                bundle["name"] = binding.nameTextInput.toString()
                userViewModel.requestRegistrationApi(bundle)
            } else{
                Toast.makeText(requireActivity(),"Please fill all required field",Toast.LENGTH_LONG).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.collectLatest {
                Toast.makeText(requireActivity(),"$it",Toast.LENGTH_LONG).show()

            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.registrationSuccessState.collectLatest {

            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.loadingState.collectLatest {

            }
        }


    }

}