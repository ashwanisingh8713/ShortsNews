package com.ns.news

import android.app.Application
import com.ns.news.data.api.NewsApi
import com.news.data.api.mappers.SectionMapper
import com.ns.news.data.repositories.NewsRepository
import com.news.utils.DefaultDispatchersProvider
import com.ns.news.presentation.activity.ArticleNdWidgetViewModelFactory
import com.ns.news.presentation.activity.SectionViewModelFactory

class NewsApplication : Application() {

    companion object {
        var newsRepository: NewsRepository? = null
    }

    override fun onCreate() {
        super.onCreate()

        newsRepository = createRepository()
        SectionViewModelFactory.inject(newsRepository!!)
        ArticleNdWidgetViewModelFactory.inject(newsRepository!!)

    }

    private fun createRepository(): NewsRepository? {
        return NewsRepository(DefaultDispatchersProvider(), SectionMapper(), NewsApi.create(this))
    }


}