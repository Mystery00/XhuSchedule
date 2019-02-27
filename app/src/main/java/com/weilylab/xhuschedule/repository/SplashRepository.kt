package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.repository.remote.SplashRemoteDataSource
import vip.mystery0.tools.utils.NetworkTools
import com.weilylab.xhuschedule.viewmodel.SplashViewModel

object SplashRepository {
	fun requestSplash(splashViewModel: SplashViewModel) {
		if (NetworkTools.isConnectInternet())
			SplashRemoteDataSource.requestSplash(splashViewModel.splash)
		else
			SplashLocalDataSource.requestSplash(splashViewModel.splash)
	}

	fun getSplash(): SplashResponse.Splash = SplashLocalDataSource.getSplash()
}