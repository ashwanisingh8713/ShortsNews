package com.ns.shortsnews.user.data.di

import com.ns.shortsnews.BuildConfig
import com.ns.shortsnews.user.data.network.createOkHttpClient
import com.squareup.moshi.Moshi
import org.koin.dsl.module
import retrofit2.converter.moshi.MoshiConverterFactory
import com.ns.shortsnews.user.data.network.createRetrofit
import com.ns.shortsnews.user.data.network.createService

val NetworkModule = module {

    single { createRetrofit(get(), BuildConfig.BASE_URL) }

    single { createService(get()) }

    single { createOkHttpClient() }

    single { MoshiConverterFactory.create() }

    single { Moshi.Builder().build() }

    single {  }
}