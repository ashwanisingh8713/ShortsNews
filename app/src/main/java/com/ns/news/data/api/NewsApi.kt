package com.news.data.api

import android.content.Context
import android.util.Log
import com.news.data.api.ApiConstants.BASE_ENDPOINT
import com.news.data.api.ApiConstants.CATEGORY
import com.news.data.api.ApiConstants.LANGUAGE
import com.news.data.api.ApiConstants.SECTION
import com.news.data.api.interceptors.NetworkStatusInterceptor
import com.news.data.api.model.CategoryResponse
import com.news.data.api.model.LanguageResponse
import com.news.data.api.model.SectionResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsApi {

  @GET("$CATEGORY/{language}")
  suspend fun getCategory(@Path("language") languageId: String = "1"): CategoryResponse

  @GET("$LANGUAGE")
  suspend fun getLanguage(): LanguageResponse

  @GET("$SECTION/{language}")
  suspend fun getSection(@Path("language") languageId: String = "1"): SectionResponse

  companion object {
    fun create(context: Context): NewsApi {
      return Retrofit.Builder()
          .baseUrl(BASE_ENDPOINT)
          .client(createOkHttpClient(context))
          .addConverterFactory(MoshiConverterFactory.create())
          .build()
          .create(NewsApi::class.java)
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
      return OkHttpClient.Builder()
          .addInterceptor(NetworkStatusInterceptor(ConnectionManager(context)))
          .addInterceptor(httpLoggingInterceptor)
          .build()
    }

    private val httpLoggingInterceptor: HttpLoggingInterceptor
      get() = HttpLoggingInterceptor { message ->
        Log.i("Network", message)
      }.apply {
        level = HttpLoggingInterceptor.Level.BODY
      }
  }
}