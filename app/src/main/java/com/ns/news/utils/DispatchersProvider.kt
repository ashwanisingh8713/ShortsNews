package com.news.utils

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProvider {
  fun io(): CoroutineDispatcher
  fun main(): CoroutineDispatcher
}