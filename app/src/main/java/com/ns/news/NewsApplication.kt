

package com.ns.news

import android.app.Application
import com.news.data.api.NewsApi
import com.news.data.api.mappers.SectionMapper
import com.ns.news.data.repositories.NewsRepository
import com.ns.news.presentation.onboarding.LanguageViewModelFactory
import com.news.utils.DefaultDispatchersProvider
import com.ns.news.presentation.onboarding.TopicViewModelFactory

class NewsApplication : Application() {

  companion object {
  var newsRepository : NewsRepository? = null
  }
  override fun onCreate() {
    super.onCreate()

      newsRepository = createRepository()

  }

    private fun createRepository(): NewsRepository? {
        return NewsRepository(DefaultDispatchersProvider(), SectionMapper(), NewsApi.create(this))
    }



}