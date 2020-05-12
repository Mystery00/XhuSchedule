package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.repository.remote.SplashRemoteDataSource
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.utils.isConnectInternet

class SplashRepository : KoinComponent {
	private val splashLocalDataSource: SplashLocalDataSource by inject()
	private val splashRemoteDataSource: SplashRemoteDataSource by inject()

	suspend fun requestSplash(): Splash = if (isConnectInternet())
		splashRemoteDataSource.requestSplash()
	else
		splashLocalDataSource.requestSplash()

	suspend fun getSplash(): Splash = splashLocalDataSource.getSplash()
}