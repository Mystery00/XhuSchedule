package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.viewmodel.BottomNavigationViewModel
import com.weilylab.xhuschedule.viewmodel.LoginViewModel
import com.weilylab.xhuschedule.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
	viewModel {
		SplashViewModel()
	}
	viewModel {
		LoginViewModel()
	}
	viewModel {
		BottomNavigationViewModel(get(), get())
	}
	viewModel {
		HistoryViewModel(get())
	}
}