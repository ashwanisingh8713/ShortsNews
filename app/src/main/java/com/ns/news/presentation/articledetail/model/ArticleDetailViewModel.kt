package com.ns.articledetail.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ns.articledetail.data.ArticleData

class ArticleDetailViewModel: ViewModel() {
    var articlesMutableList = mutableListOf<ArticleData>()
    var fragmentValueMutableData = MutableLiveData<ArticleData>()
    val articleList = MutableLiveData<List<ArticleData>>()
    val fragmentValueLiveData:LiveData<ArticleData> get()  = fragmentValueMutableData
    val articleDetailLiveData: LiveData<List<ArticleData>> get() = articleList

    init {
        createArticleDetailData()
    }
    fun createArticleDetailData(){
        articlesMutableList.add(ArticleData("1","First"))
        articlesMutableList.add(ArticleData("2","Second"))
        articlesMutableList.add(ArticleData("3","Third"))
        articlesMutableList.add(ArticleData("4","Four"))
        articlesMutableList.add(ArticleData("5","Five"))
        articlesMutableList.add(ArticleData("6","Six"))
        articlesMutableList.add(ArticleData("7","Seven"))
        articlesMutableList.add(ArticleData("8","Eight"))
        articlesMutableList.add(ArticleData("9","Nine"))
        articlesMutableList.add(ArticleData("10","Ten"))
        articleList.value = articlesMutableList
    }
    fun getArticleValue( position: Int){
        fragmentValueMutableData.value = articlesMutableList.get(position)
    }
}

