/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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