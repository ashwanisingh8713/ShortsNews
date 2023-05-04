package com.ns.shortsnews.user.data.network

import com.ns.shortsnews.MainActivity
import com.ns.shortsnews.MainApplication
import com.ns.shortsnews.user.data.source.UserApiService
import com.ns.shortsnews.utils.PrefUtils
import com.ns.shortsnews.utils.Validation
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


private const val TIME_OUT = 30L

fun createRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create()).build()
}


fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder().also {
                        val context = MainApplication.applicationContext()
                        if (PrefUtils.with(context = context).getBoolean(Validation.PREF_IS_USER_LOGGED_IN, false) ){
                            val token = "Bearer ${PrefUtils.with(context).getString(Validation.PREF_USER_TOKEN,"")}"
                            if (token.isNotEmpty()){
                                it.addHeader("Authorization", token)
                            }
                        }
                    }
                    .build()
            )
        }
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

fun createService(retrofit: Retrofit): UserApiService {
    return retrofit.create(UserApiService::class.java)
}


