package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.repository.remote.SplashRemoteDataSource
import com.weilylab.xhuschedule.utils.NetworkUtil
import com.weilylab.xhuschedule.viewmodel.SplashViewModel

object SplashRepository {
	fun requestSplash(splashViewModel: SplashViewModel) {
		if (NetworkUtil.isConnectInternet())
			SplashRemoteDataSource.requestSplash(splashViewModel.splash)
		else
			SplashLocalDataSource.requestSplash(splashViewModel.splash)
	}

	fun getSplash(): SplashResponse.Splash = SplashLocalDataSource.getSplash()
}