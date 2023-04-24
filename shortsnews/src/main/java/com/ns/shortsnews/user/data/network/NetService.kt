package com.ns.shortsnews.user.data.network

import com.ns.shortsnews.user.data.source.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class NetService {
    companion object{
        private const val TIME_OUT = 30L
        private const val BASE_URL = "https://shorts.newsdx.io/ci/api/public/"
    }


    fun createRetrofit(): UserApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create()).build()
        return retrofit.create(UserApiService::class.java)
    }


    private fun createOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder()
            .addInterceptor { chain -> chain.proceed(
                chain.request()
                    .newBuilder()
//                    .also {
//                        it.addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjbGllbnRfaWQiOjEsInByb3BlcnR5X2lkIjoxLCJ0aW1lIjoxNjc5MDU0MTQwfQ.rJn83qJutwRyeTY5-F76BBhH3_sMY3vXPRe8Xu64QZU")
//                    }
                    .build()) }
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)

            .addInterceptor(httpLoggingInterceptor).build()
    }

//    fun createService(retrofit: Retrofit): UserApiService {
//        return retrofit.create(UserApiService::class.java)
//    }
}

