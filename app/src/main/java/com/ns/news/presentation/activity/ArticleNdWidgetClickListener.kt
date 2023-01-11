package com.ns.news.presentation.activity


fun interface ArticleNdWidgetClickListener {
  fun onArticleClick(cell_type: String, type: String, sectionId: String, articleId: String)
}
