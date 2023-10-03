package com.ns.shortsnews.data.di

import com.ns.shortsnews.BuildConfig
import com.ns.shortsnews.data.network.createOkHttpClient
import com.squareup.moshi.Moshi
import org.koin.dsl.module
import retrofit2.converter.moshi.MoshiConverterFactory
import com.ns.shortsnews.data.network.createRetrofit
import com.ns.shortsnews.data.network.createService
import com.ns.shortsnews.data.source.UserApiService

val NetworkModule = module {

    single { createRetrofit(get(), BuildConfig.BASE_URL) }

    single<UserApiService> { createService(get()) }

    single { createOkHttpClient() }

    single { MoshiConverterFactory.create() }

    single { Moshi.Builder().build() }

    single {  }
}