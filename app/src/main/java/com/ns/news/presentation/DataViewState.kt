package com.news.presentation

data class DataViewState(
    val loading: Boolean = true,
    val coins: List<String> = emptyList()
)

