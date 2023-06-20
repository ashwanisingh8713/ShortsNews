package com.ns.shortsnews.video.data

import com.ns.shortsnews.BuildConfig
import com.ns.shortsnews.utils.AppPreference
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Ashwani Kumar Singh on 29,May,2023.
 */
object VideoDataNetService {

    val videoDataApiService: VideoDataService = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(
            OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder().also {
                                if (AppPreference.isUserLoggedIn) {
                                    val token = "Bearer ${AppPreference.userToken}"
                                    if (token.isNotEmpty()) {
                                        it.addHeader("Authorization", token)
                                    }
                                }
                            }
                            .build()
                    )
                }.build()
        )
        .build()
        .create(VideoDataService::class.java)
}