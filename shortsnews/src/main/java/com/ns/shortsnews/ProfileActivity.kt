package com.ns.shortsnews

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope

import com.ns.shortsnews.databinding.ActivityProfileBinding
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.user.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.user.ui.fragment.LanguageFragment
import com.ns.shortsnews.user.ui.fragment.LoginFragment
import com.ns.shortsnews.user.ui.fragment.OtpFragment
import com.ns.shortsnews.user.ui.fragment.UserProfileFragment
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val sharedUserViewModel: UserViewModel by viewModels {
        UserViewModelFactory().apply {
            inject(
                UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
                UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
                LanguageDataUseCase(UserDataRepositoryImpl(get())),
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = Color.parseColor("#000000")
        window.navigationBarColor = Color.parseColor("#000000")
        val from = intent.getStringExtra("fromActivity")

        if (AppPreference.isUserLoggedIn) {
            if (from !=null && from == "MainActivity"){
                choiceFragment()
            } else{
                launchMainActivityIntent()
            }
        } else {
            loginFragment()
        }
        listenFragmentUpdate()
    }

    private fun listenFragmentUpdate() {
        lifecycleScope.launch() {
            sharedUserViewModel.fragmentStateFlow.filterNotNull().collectLatest {
                val bundle = it
                when (bundle.getString("fragmentType")) {
                    UserViewModel.PROFILE -> choiceFragment()
                    UserViewModel.OTP -> otpFragment(bundle)
                    UserViewModel.LOGIN -> loginFragment()
                    UserViewModel.OTP_POP -> popOtpFragment()
                    UserViewModel.MAIN_ACTIVITY -> launchMainActivityIntent()
                    UserViewModel.LANGUAGES -> languagesFragment()
                }
            }
        }
    }

    private fun loginFragment() {
        val fra = LoginFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fra)
            .addToBackStack("login").commit()
    }

    private fun popOtpFragment() {
        supportFragmentManager.popBackStack()
    }


    private fun otpFragment(bundle: Bundle) {
        val fragment = OtpFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.fragment_containerProfile, fragment)
            .addToBackStack("opt").commit()
    }


    private fun choiceFragment() {
        val fragment = UserProfileFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment)
            .commit()
    }

    private fun languagesFragment() {
        val fragment = LanguageFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment)
            .commit()
    }


    private fun launchMainActivityIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}