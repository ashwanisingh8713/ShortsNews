package com.ns.shortsnews

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ns.shortsnews.data.model.VideoClikedItem
import com.ns.shortsnews.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.databinding.ActivityLauncherBinding
import com.ns.shortsnews.domain.models.LanguageData
import com.ns.shortsnews.domain.models.VideoCategory
import com.ns.shortsnews.domain.usecase.language.LanguageDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserOtpValidationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserRegistrationDataUseCase
import com.ns.shortsnews.domain.usecase.user.UserSelectionsDataUseCase
import com.ns.shortsnews.domain.usecase.video_category.UpdateVideoCategoriesUseCase
import com.ns.shortsnews.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.ui.viewmodel.UserViewModel
import com.ns.shortsnews.ui.viewmodel.UserViewModelFactory
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.AppConstants
import com.ns.shortsnews.utils.AppPreference
import com.videopager.utils.CategoryConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class LauncherActivity : AppCompatActivity() {
    lateinit var binding: ActivityLauncherBinding

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory().apply {
            inject(
                UserRegistrationDataUseCase(UserDataRepositoryImpl(get())),
                UserOtpValidationDataUseCase(UserDataRepositoryImpl(get())),
                LanguageDataUseCase(UserDataRepositoryImpl(get())),
                UserSelectionsDataUseCase(UserDataRepositoryImpl(get()))
            )
        }
    }

    private val videoCategoryViewModel: VideoCategoryViewModel by viewModels {
        VideoCategoryViewModelFactory().apply {
            inject(
                VideoCategoryUseCase(VideoCategoryRepositoryImp(get())),
                UpdateVideoCategoriesUseCase(VideoCategoryRepositoryImp(get()))
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)


        lifecycleScope.launch() {
            userViewModel.LanguagesSuccessState.filterNotNull().collectLatest {
                Log.i("kamlesh", "Language onSuccess ::: $it")
                it.let {
                    if (it.isNotEmpty()) {
                        videoCategoryViewModel.loadVideoCategory()
                    }
                }
            }
        }
        lifecycleScope.launch() {
            userViewModel.errorState.filterNotNull().collectLatest {
                Log.i("kamlesh", "OTPFragment onError ::: $it")
            }
        }
        lifecycleScope.launch {
            userViewModel.loadingState.filterNotNull().collectLatest {
                Log.i("kamlesh", "Launcher activity loadingState ::: $it")
                binding.progressBarLauncher.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().filter {
                it.videoCategories.isNotEmpty()
            }.collectLatest {
                // Save category data in preference
                getSelectedVideoInterstCategory(it.videoCategories as MutableList<VideoCategory>)
            }
        }


        if (!AppPreference.isUserLoggedIn) {
            launchProfileActivity()
        } else {
            if (AppPreference.isLanguageSelected) {
//                if (intent?.extras != null){
//                    Log.i("intent_newLaunch","Launcher activity extras is there ")
//                    val videoId = intent.getStringExtra(AppConstants.ID)!!
//                    val type = intent.getStringExtra(AppConstants.TYPE)!!
//                    val previewUrl = intent.getStringExtra(AppConstants.VIDEO_PREVIEW_URL)!!
//                    val videoUrl = intent.getStringExtra(AppConstants.VIDEO_URL)!!
//                    Log.i("intent_newLaunch","Launcher activity extras is $videoId$videoUrl$previewUrl ")
//
//                    launchMainActivityWithExtras( videoId, type, previewUrl, videoUrl)
//                } else {
                if (AppPreference.getSelectedLanguages().isEmpty()) {
                    userViewModel.requestLanguagesApi()
                } else {
                    launchMainActivity()
                }
//                }
            } else {
                launchProfileActivity()
            }
        }


    }


    private fun launchProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        this.finish()
    }


    private fun getSelectedVideoInterstCategory(categoryList: MutableList<VideoCategory>) {
        val unselectedCategory = mutableListOf<VideoCategory>()
        val selectedCategory = mutableListOf<VideoCategory>()

        for (item in categoryList) {
            if (item.default_select) {
                selectedCategory.add(item)
            } else {
                unselectedCategory.add(item)
            }
        }
        val finalList: List<VideoCategory> = selectedCategory + unselectedCategory
        AppPreference.saveCategoriesToPreference(finalList)
        AppPreference.init(this@LauncherActivity)
        AppPreference.isRefreshRequired = false
        launchMainActivity()
    }
}