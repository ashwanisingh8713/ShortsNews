package com.ns.shortsnews

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope

import com.ns.shortsnews.databinding.ActivityProfileBinding
import com.ns.shortsnews.user.data.network.NetService
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCases
import com.ns.shortsnews.user.domain.usecase.user.UserProfileDataUseCases
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCases
import com.ns.shortsnews.user.ui.fragment.LoginFragment
import com.ns.shortsnews.user.ui.fragment.OtpFragment
import com.ns.shortsnews.user.ui.fragment.UserFragment
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    companion object {
        private val netService = NetService()
        private val userDataRepository = UserDataRepositoryImpl(netService.createRetrofit())
        private val userRegistrationDataUseCases = UserRegistrationDataUseCases(userDataRepository)
        private val userOtpValidationDataUseCases = UserOtpValidationDataUseCases(userDataRepository)
        private val userProfileDataUseCases = UserProfileDataUseCases(userDataRepository)

        val userViewModelFactory = UserViewModelFactory().apply {
            inject(
                userRegistrationDataUseCases,
                userOtpValidationDataUseCases,
                userProfileDataUseCases
            )
        }
    }
    val userViewModel: UserViewModel by viewModels {userViewModelFactory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = Color.parseColor("#1E1E1E")
        window.navigationBarColor = Color.parseColor("#1E1E1E")

        loginFragment()

        listenFragmentUpdate()
    }

    private fun listenFragmentUpdate() {
        lifecycleScope.launch() {
            userViewModel.fragmentStateFlow.collectLatest {
                when(it) {
                    UserViewModel.PROFILE-> ""
                    UserViewModel.OTP->""
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
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment).commit()
    }


    private fun profileFragment() {
        val fragment = UserFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment).commit()
    }



}