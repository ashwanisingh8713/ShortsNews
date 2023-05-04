package com.ns.shortsnews

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope

import com.ns.shortsnews.databinding.ActivityProfileBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.user.ui.fragment.LoginFragment
import com.ns.shortsnews.user.ui.fragment.OtpFragment
import com.ns.shortsnews.user.ui.fragment.UserProfileFragment
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.PrefUtils
import com.ns.shortsnews.utils.Validation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val sharedUserViewModel: UserViewModel by viewModels {UserViewModelFactory().apply {
        inject(
            UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
            UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
        )
    }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = Color.parseColor("#1E1E1E")
        window.navigationBarColor = Color.parseColor("#1E1E1E")

        if (PrefUtils.with(this).getBoolean(Validation.PREF_IS_USER_LOGGED_IN, false)){
            choiceFragment()
        } else{
            loginFragment()
        }
        listenFragmentUpdate()


    }

    private fun listenFragmentUpdate() {
        lifecycleScope.launch() {
            sharedUserViewModel.fragmentStateFlow.filterNotNull().collectLatest {
                val bundle = it
                when(bundle.getString("fragmentType")) {
                    UserViewModel.PROFILE-> choiceFragment()
                    UserViewModel.OTP-> otpFragment(bundle)
                    UserViewModel.LOGIN-> loginFragment()
                }
            }
        }
    }

    private fun loginFragment() {
        val fra = LoginFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fra).commit()
    }


    private fun otpFragment(bundle: Bundle) {
        val fragment = OtpFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.fragment_containerProfile, fragment).commit()
    }


    private fun choiceFragment() {
        val fragment = UserProfileFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment).commit()
    }
}