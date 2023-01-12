package com.ns.news.presentation.activity

import com.ns.news.data.db.Cell


fun interface ArticleNdWidgetClickListener {
  fun onArticleClick(cell: Cell, articleId: String)
}
