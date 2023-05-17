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
import com.ns.shortsnews.utils.PrefUtils
import com.ns.shortsnews.utils.Validation
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.SharedEventViewModel
import com.videopager.vm.SharedEventViewModelFactory
import com.videopager.vm.VideoPagerViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity(), onProfileItemClick{
    private lateinit var binding: ActivityMainBinding
    private lateinit var caAdapter: CategoryAdapter

    private val sharedEventViewModel: SharedEventViewModel by viewModels { SharedEventViewModelFactory }

    private val videoRepository = VideoCategoryRepositoryImp(get())
    private val videoCategoryUseCase = VideoCategoryUseCase(videoRepository)

    private val userViewModelFactory = VideoCategoryViewModelFactory().apply {
        inject(
            videoCategoryUseCase
        )
    }

    private val videoCategoryViewModel: VideoCategoryViewModel by viewModels { userViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val view = binding.root
        setContentView(view)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        val isUserLoggedIn = PrefUtils.with(this@MainActivity).getBoolean(Validation.PREF_IS_USER_LOGGED_IN, false)
        if(isUserLoggedIn){
            val profileImage = PrefUtils.with(this@MainActivity).getString(Validation.PREF_USER_IMAGE, "")
            binding.profileIcon.load(profileImage)
        }


        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
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


    /**
     * Loads Home Fragment
     */
    private fun loadHomeFragment(shortsType: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, makeVideoPagerInstance(shortsType))
        ft.commit()
         var isUserLoggedIn = PrefUtils.with(this@MainActivity).getBoolean(Validation.PREF_IS_USER_LOGGED_IN, false)
        var userToken = PrefUtils.with(this@MainActivity).getUserToken()
        if (userToken != null) {
            PrefUtils.USER_TOKEN = userToken
        }

        sharedEventViewModel.sendUserPreferenceData(isUserLoggedIn,"" )

//        registerVideoCache()

    }
    private fun launchLoginStateFlow() {
        lifecycleScope.launch {
            sharedEventViewModel.getLoginEventStatus.collectLatest {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
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






}