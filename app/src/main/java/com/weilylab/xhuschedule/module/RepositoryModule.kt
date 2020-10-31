/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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