package com.ns.shortsnews

import android.annotation.SuppressLint
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
import com.ns.shortsnews.domain.usecase.notification.FCMTokenDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
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
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (intent?.extras != null){
            getData(intent)
        }
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

    private fun getData(data: Intent?) {
        val intent = Intent(this, MainActivity::class.java)
        val id = intent.putExtra("videoId",data?.getStringExtra("id").toString())
        val type = intent.putExtra("type",data?.getStringExtra("type").toString())
        val previewUrl = intent.putExtra("preview_url",data?.getStringExtra("videoPreviewUrl").toString())
        val video_url = intent.putExtra("video_url", data?.getStringExtra("video_url").toString())
        Log.i("intent_newLaunch","From profile activity :: $id $type $previewUrl $video_url")
        startActivity(intent)
        this.finish()
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

    @SuppressLint("CommitTransaction")
    private fun languagesFragment() {
        val fragment = LanguageFragment()
        val bundle = Bundle()
        bundle.putString("from", AppConstants.FROM_PROFILE)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, fragment)
            .commit()
    }


    private fun launchMainActivityIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onResume() {
        super.onResume()
        if (AppPreference.isProfileDeleted){
            AppPreference.clear()
            val i = Intent(this, ProfileActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)

        }
    }
}