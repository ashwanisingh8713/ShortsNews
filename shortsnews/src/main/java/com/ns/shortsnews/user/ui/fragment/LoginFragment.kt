package com.ns.shortsnews.user.ui.fragment

import android.annotation.SuppressLint
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
import kotlinx.coroutines.flow.filterNotNull
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

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        binding.backButtonLogin.setOnClickListener {
            activity?.finish()
        }
        binding.submitButton.setOnClickListener{
            val name = binding.nameTextInput.text.toString()
            val email = binding.emailTextInput.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty()) {
                if (Validation.validateEmail(email)){
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                    val bundle:MutableMap<String, String> = mutableMapOf()
                    bundle["email"] = email
                    bundle["name"] = name
                    userViewModel.requestRegistrationApi(bundle)
                } else {
                  val toast =   Toast.makeText(requireActivity(),"Please enter valid Email id",Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM, 0, 0)
                    toast.show()
                }
            } else{
                val toast =   Toast.makeText(requireActivity(),"Please fill all required field",Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                toast.show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.errorState.filterNotNull().collectLatest {
              if(it != "NA"){
                Toast.makeText(requireActivity(),"$it",Toast.LENGTH_LONG).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(){
            userViewModel.registrationSuccessState.filterNotNull().collectLatest {
                it.let {
                    Log.i("kamlesh","Registration Response ::: $it")
                    binding.progressBarLogin.visibility = View.GONE
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
                    binding.progressBarLogin.visibility = View.VISIBLE
                }
            }
        }


    }

}