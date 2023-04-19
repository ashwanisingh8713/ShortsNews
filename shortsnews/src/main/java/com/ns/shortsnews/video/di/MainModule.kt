package com.ns.shortsnews.video.di

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import coil.imageLoader
import com.ns.shortsnews.video.data.RedditVideoDataRepository
import com.exo.players.ExoAppPlayerFactory
import com.exo.ui.ExoAppPlayerViewFactory
import com.videopager.ui.VideoPagerFragment
import com.videopager.vm.VideoPagerViewModelFactory

 class MainModule(activity: ComponentActivity) {
    val fragmentFactory: FragmentFactory = object : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return when (loadFragmentClass(classLoader, className)) {
                VideoPagerFragment::class.java -> VideoPagerFragment(
                    viewModelFactory = { owner ->
                        VideoPagerViewModelFactory(
                            repository = RedditVideoDataRepository(),
                            appPlayerFactory = ExoAppPlayerFactory(
                                context = activity.applicationContext
                            )
                        ).create(owner)
                    },
                    appPlayerViewFactory = ExoAppPlayerViewFactory(),
                    imageLoader = activity.imageLoader
                )
                else -> super.instantiate(classLoader, className)
            }
        }
    }
}
