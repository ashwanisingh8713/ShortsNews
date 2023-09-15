package com.ns.shortsnews.data.di

import com.ns.shortsnews.ui.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
  viewModel { UserViewModel(get(), get(), get(), get()) }
}