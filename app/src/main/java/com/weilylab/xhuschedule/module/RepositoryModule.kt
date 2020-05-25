package com.weilylab.xhuschedule.module

import com.weilylab.xhuschedule.repository.SplashRepository
import org.koin.dsl.module

val repositoryModule = module {
	single { SplashRepository() }
}