package com.ns.shortsnews

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.databinding.ActivityProfileBinding
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.ui.fragment.*
import com.ns.shortsnews.ui.viewmodel.*
import com.ns.shortsnews.utils.AppConstants
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
                UserSelectionsDataUseCase(UserDataRepositoryImpl(get()))
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = Color.parseColor("#000000")
        window.navigationBarColor = Color.parseColor("#000000")
        val from = intent.getStringExtra("fromActivity")
        if (AppPreference.isUserLoggedIn) {
            if (from != null && from == "MainActivity") {
                profileFragment()
            } else {
                if (AppPreference.isLanguageSelected) {
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
        Log.i("lifecycle", "Profile activity on login fragment called")
        val fra = LoginFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_containerProfile, fra)
            .addToBackStack("login").commit()
    }

    private fun popOtpFragment() {
        Log.i("lifecycle", "Profile activity on pop otp fragment called")
        supportFragmentManager.popBackStack()
    }

    private fun otpFragment(bundle: Bundle) {
        Log.i("lifecycle", "Profile activity on OTP fragment launch called")
        val fragment = OtpFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.fragment_containerProfile, fragment)
            .addToBackStack("opt").commit()
    }


    private fun profileFragment() {
        Log.i("lifecycle", "Profile activity on new Profile fragment called")
        val fragment = NewProfileFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment)
            .commit()
    }

    @SuppressLint("CommitTransaction")
    private fun languagesFragment() {
        Log.i("lifecycle", "Profile activity on language fragment called")
        val fragment = LanguageFragment()
        val bundle = Bundle()
        bundle.putString("from", AppConstants.FROM_PROFILE)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment)
            .commit()
    }


    private fun launchMainActivityIntent() {
        Log.i("lifecycle", "Profile activity on main activity called")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onResume() {
        super.onResume()
        Log.i("lifecycle", "Profile activity on resume called")
        if (AppPreference.isProfileDeleted) {
            AppPreference.clear()
            val i = Intent(this, ProfileActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Log.i("lifecycle", "Profile activity on backPressed called")
        val fragmentManager: FragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            // If there are fragments in the back stack, and if it is login fragment so closing container activity
            val topFragment = supportFragmentManager.findFragmentById(R.id.fragment_containerProfile)
            if (topFragment is LoginFragment){
                finish()
            } else {
                // If there are fragments in the back stack, pop the top fragment
                fragmentManager.popBackStack()
            }

        } else {
            // If there are no fragments in the back stack, finish the activity
            Log.i("lifecycle", "Profile activity on backPressed activity finish called")
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
    }
}