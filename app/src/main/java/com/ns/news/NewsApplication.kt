package com.ns.news

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.ns.news.data.api.NewsApi
import com.ns.news.data.mappers.DataMapper
import com.ns.news.data.repositories.NewsRepository
import com.news.utils.DefaultDispatchersProvider
import com.ns.news.data.db.NewsDb
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.activity.ui.home.ArticleNdWidgetViewModelFactory
import com.ns.news.presentation.activity.SectionViewModelFactory
import com.ns.news.presentation.activity.ui.detail.ArticleDetailViewModelFactory
import com.ns.news.presentation.activity.ui.home.SectionDBViewModelFactory

class NewsApplication : Application() {

    companion object {
        lateinit var newsRepository: NewsRepository
        lateinit var newsDb: NewsDb
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        newsDb = createDB()
        newsRepository = createRepository(newsDb)
        SectionViewModelFactory.inject(newsRepository)
        SectionDBViewModelFactory.inject(newsDb.sectionDao())
        ArticleDetailViewModelFactory.inject(newsDb.cellItems())
        ArticleNdWidgetViewModelFactory.inject(newsRepository, newsDb.readDao())
        NewsSharedViewModelFactory.inject(newsDb.readDao(), newsDb.bookmarkDao())

    }

    private fun createDB() : NewsDb{
        return NewsDb.create(this)
    }

    private fun createRepository(newsDb: NewsDb): NewsRepository {
        return NewsRepository(DefaultDispatchersProvider(), DataMapper(), NewsApi.create(), newsDb)
    }


}