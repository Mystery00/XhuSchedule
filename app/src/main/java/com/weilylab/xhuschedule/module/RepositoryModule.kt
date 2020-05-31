package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.repository.*
import org.koin.dsl.module

val repositoryModule = module {
	single { BottomNavigationRepository() }
	single { CourseRepository() }
	single { CustomCourseRepository() }
	single { CustomThingRepository() }
	single { FeedBackRepository() }
	single { InitRepository() }
	single { NoticeRepository() }
	single { NotificationRepository() }
	single { SchoolCalendarRepository() }
	single { ScoreRepository() }
	single { SplashRepository() }
	single { StudentRepository() }
	single { TestRepository() }
	single { WidgetRepository() }
}