package com.ns.news.data.api

import android.content.Context
import android.util.Log
import com.news.data.api.ApiConstants.BASE_ENDPOINT
import com.news.data.api.ApiConstants.CATEGORY
import com.news.data.api.ApiConstants.LANGUAGE
import com.news.data.api.ApiConstants.SECTION
import com.news.data.api.ConnectionManager
import com.ns.news.data.api.interceptors.NetworkStatusInterceptor
import com.news.data.api.model.CategoryResponse
import com.ns.news.data.api.model.ArticleNdWidgetResponse
import com.ns.news.data.api.model.LanguageResponse
import com.ns.news.data.api.model.SectionResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface NewsApi {

  @GET("$CATEGORY/{language}")
  suspend fun getCategory(@Path("language") languageId: String = "1"): CategoryResponse

  @GET("$LANGUAGE")
  suspend fun getLanguage(): LanguageResponse

  @GET("$SECTION/{language}")
  suspend fun getSection(@Path("language") languageId: String): SectionResponse

  @GET("")
  suspend fun getArticleNdWidget(@Url url: String): ArticleNdWidgetResponse

  companion object {
    fun create(): NewsApi {
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Log.d("API", it) })
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
      return Retrofit.Builder()
          .baseUrl(BASE_ENDPOINT)
          .client(client)
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