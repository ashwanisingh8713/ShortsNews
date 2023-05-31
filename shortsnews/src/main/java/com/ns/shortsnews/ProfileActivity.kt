package com.ns.shortsnews

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope

import com.ns.shortsnews.databinding.ActivityProfileBinding
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.ui.fragment.LanguageFragment
import com.ns.shortsnews.ui.fragment.LoginFragment
import com.ns.shortsnews.ui.fragment.NewProfileFragment
import com.ns.shortsnews.ui.fragment.OtpFragment
import com.ns.shortsnews.ui.viewmodel.ProfileSharedViewModel
import com.ns.shortsnews.ui.viewmodel.ProfileSharedViewModelFactory
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.utils.AppPreference
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val sharedViewModel: ProfileSharedViewModel by viewModels { ProfileSharedViewModelFactory }

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
                profileFragment()
            } else{
                if(AppPreference.isLanguageSelected) {
                    launchMainActivityIntent()
                } else {
                    languagesFragment()
                }
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
                Log.i("fragment", "${bundle.getString("fragmentType")}")
                when (bundle.getString("fragmentType")) {
                    UserViewModel.PROFILE -> profileFragment()
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


    private fun profileFragment() {
        val fragment = NewProfileFragment()
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