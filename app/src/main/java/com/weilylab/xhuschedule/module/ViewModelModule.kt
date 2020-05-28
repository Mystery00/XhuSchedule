package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.viewmodel.*
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
		BottomNavigationViewModel()
	}
	viewModel {
		SettingsViewModel()
	}
	viewModel {
		ClassCourseColorViewModel()
	}
	viewModel {
		CustomCourseViewModel()
	}
}