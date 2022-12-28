package com.news.utils

import kotlinx.coroutines.Dispatchers

class DefaultDispatchersProvider : DispatchersProvider {

  override fun io() = Dispatchers.IO
  override fun main() = Dispatchers.Main
}