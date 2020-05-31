package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
	viewModel {
		BottomNavigationViewModel()
	}
	viewModel {
		ClassCourseColorViewModel()
	}
	viewModel {
		CustomCourseViewModel()
	}
	viewModel {
		CustomThingViewModel()
	}
	viewModel {
		FeedBackViewModel()
	}
	viewModel {
		LoginViewModel()
	}
	viewModel {
		NoticeViewModel()
	}
	viewModel {
		QueryClassroomViewModel()
	}
	viewModel {
		QueryClassScoreViewModel()
	}
	viewModel {
		QueryExpScoreViewModel()
	}
	viewModel {
		QueryTestViewModel()
	}
	viewModel {
		SettingsViewModel()
	}
	viewModel {
		SplashViewModel()
	}
}