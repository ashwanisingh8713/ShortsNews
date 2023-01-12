package com.ns.news.presentation.activity


fun interface ArticleNdWidgetClickListener {
  fun onArticleClick(cellType: String, type: String, sectionId: String, articleId: String)
}
