package com.ns.shortsnews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import coil.imageLoader
import coil.load
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.ns.shortsnews.adapters.CategoryAdapter
import com.ns.shortsnews.cache.VideoPreloadWorker
import com.ns.shortsnews.databinding.ActivityMainBinding
import com.ns.shortsnews.user.data.repository.VideoCategoryRepositoryImp
import com.ns.shortsnews.user.domain.usecase.video_category.VideoCategoryUseCase
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModel
import com.ns.shortsnews.user.ui.viewmodel.VideoCategoryViewModelFactory
import com.ns.shortsnews.utils.AppPreference
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity(), onProfileItemClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter
    private val sharedEventViewModel: SharedEventViewModel by viewModels { SharedEventViewModelFactory }
    private val videoCategoryViewModel: VideoCategoryViewModel by viewModels { VideoCategoryViewModelFactory().apply {
        inject(
            VideoCategoryUseCase(VideoCategoryRepositoryImp(get()))
        )
    } }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val view = binding.root
        setContentView(view)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        if (AppPreference.isUserLoggedIn) {
            binding.profileIcon.load(AppPreference.userProfilePic)
        }

        binding.profileIcon.setOnClickListener {
            launchProfileIntent()
        }
        videoCategoryViewModel.loadVideoCategory()
        showCategory()
        // To get status of should launch in login flow for non logged-in user
        launchLoginStateFlow()
    }

    /**
     * It listens category item click
     */
    override fun itemclick(shortsType: String, position:Int, size:Int) {
        loadHomeFragment(shortsType)
    }

    private fun launchProfileIntent(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }


    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(shortsType: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, makeVideoPagerInstance(shortsType))
        ft.commit()
        AppPreference.userToken?.let {
            sharedEventViewModel.sendUserPreferenceData(
                AppPreference.isUserLoggedIn,
                it
            )
        }

//        registerVideoCache()

    }

    private fun launchLoginStateFlow() {
        lifecycleScope.launch {
            sharedEventViewModel.getLoginEventStatus.collectLatest {
                launchProfileIntent()
            }
        }
    }


    /**
     * Creates VideoPagerFragment Instance
     */
    private fun makeVideoPagerInstance(shortsType: String): VideoPagerFragment {
       val appPlayerView =  ExoAppPlayerViewFactory().create(this@MainActivity)
        val vpf =  VideoPagerFragment(
            viewModelFactory = { owner ->
                VideoPagerViewModelFactory(
                    repository = VideoDataRepositoryImpl(),
                    appPlayerFactory = ExoAppPlayerFactory(
                        context = this@MainActivity, cache = MainApplication.cache
                    )
                ).create(owner)
            },
            appPlayerView = appPlayerView,
            imageLoader = this@MainActivity.imageLoader,
            shortsType = shortsType
        )
        return vpf
    }

    private fun showCategory() {
        lifecycleScope.launch {
            videoCategoryViewModel.videoCategorySuccessState.filterNotNull().collectLatest {
                // Setup recyclerView
                caAdapter = CategoryAdapter(
                    itemList = it.videoCategories,
                    itemListener = this@MainActivity
                )
                binding.recyclerView.adapter = caAdapter
                val defaultCate = it.videoCategories[0]
                loadHomeFragment(defaultCate.id)
            }
        }
    }


    private fun registerVideoCache() {
        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl!!.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1){url}
                var videoIds = Array(1){id}
                VideoPreloadWorker.schedulePreloadWork(videoUrls=videoUrls, ids=videoIds)
            }
        }

        lifecycleScope.launch {
            sharedEventViewModel.cacheVideoUrl_2.filterNotNull().collectLatest {
                val url = it.first
                val id = it.second
                var videoUrls = Array(1){url}
                var videoIds = Array(1){id}
                VideoPreloadWorker.schedulePreloadWork(videoUrls=videoUrls, ids=videoIds)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!AppPreference.isUserLoggedIn) {
            sharedEventViewModel.sendUserPreferenceData(false,"")
        } else {
            sharedEventViewModel.sendUserPreferenceData(AppPreference.isUserLoggedIn,AppPreference.userToken!!)
        }
    }
}