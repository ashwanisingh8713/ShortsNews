package com.ns.shortsnews.video.di

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import coil.imageLoader
import com.ns.shortsnews.video.data.VideoDataRepositoryImpl
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.ns.shortsnews.MainApplication
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.VideoPagerViewModelFactory

 class MainModule(activity: ComponentActivity) {
    val fragmentFactory: FragmentFactory = object : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return when (loadFragmentClass(classLoader, className)) {
                VideoPagerFragment::class.java -> VideoPagerFragment(
                    viewModelFactory = { owner ->
                        VideoPagerViewModelFactory(
                            repository = VideoDataRepositoryImpl(),
                            appPlayerFactory = ExoAppPlayerFactory(
                                context = activity.applicationContext, cache = MainApplication.cache
                            )
                        ).create(owner)
                    },
                    appPlayerViewFactory = ExoAppPlayerViewFactory(),
                    imageLoader = activity.imageLoader,
                    shortsType = ""
                )
                else -> super.instantiate(classLoader, className)
            }
        }
    }
}
