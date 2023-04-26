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
import com.ns.shortsnews.user.ui.fragment.UserChoiceFragment
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import kotlinx.coroutines.flow.collectLatest
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

//        loginFragment()
        choiceFragment()
        listenFragmentUpdate()
    }

    private fun listenFragmentUpdate() {
        lifecycleScope.launch() {
            sharedUserViewModel.fragmentStateFlow.collectLatest {
                when(it) {
                    UserViewModel.PROFILE-> choiceFragment()
                    UserViewModel.OTP-> otpFragment()
                    UserViewModel.LOGIN-> loginFragment()
                }
            }
        }
    }

    private fun loginFragment() {
        val fra = LoginFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fra).commit()
    }


    private fun otpFragment() {
        val fragment = OtpFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_containerProfile, fragment).commit()
    }


    private fun choiceFragment() {
        val fragment = UserChoiceFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment).commit()
    }
}