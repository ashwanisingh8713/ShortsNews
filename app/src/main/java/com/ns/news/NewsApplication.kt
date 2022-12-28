

package com.ns.news

import android.app.Application
import com.news.data.api.NewsApi
import com.news.data.api.mappers.SectionMapper
import com.ns.news.data.repositories.NewsRepository
import com.news.presentation.viewmodel.LanguageViewModelFactory
import com.news.utils.DefaultDispatchersProvider

class NewsApplication : Application() {

  override fun onCreate() {
    super.onCreate()

      val newsRepository = createRepository()
      LanguageViewModelFactory.inject(newsRepository!!)
  }

    private fun createRepository(): NewsRepository? {
        return NewsRepository(DefaultDispatchersProvider(), SectionMapper(), NewsApi.create(this))
    }


}