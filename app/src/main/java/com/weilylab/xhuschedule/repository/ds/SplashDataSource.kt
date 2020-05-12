package com.weilylab.xhuschedule.repository.ds

import com.weilylab.xhuschedule.model.Splash

interface SplashDataSource {
	suspend fun requestSplash(): Splash
}