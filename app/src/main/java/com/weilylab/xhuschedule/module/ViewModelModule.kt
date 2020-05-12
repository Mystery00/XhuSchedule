package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
	viewModel {
		SplashViewModel(get())
	}
	viewModel {
		MainViewModel(get(), get())
	}
	viewModel {
		HistoryViewModel(get())
	}
}