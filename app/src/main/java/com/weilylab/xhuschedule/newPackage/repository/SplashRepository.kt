package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.model.response.SplashResponse
import com.weilylab.xhuschedule.newPackage.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.SplashRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.NetworkUtil
import com.weilylab.xhuschedule.newPackage.viewModel.SplashViewModel

object SplashRepository {
	const val DONE = 21
	const val ERROR = 22

	fun requestSplash(splashViewModel: SplashViewModel) {
		if (NetworkUtil.isConnectInternet())
			SplashRemoteDataSource.requestSplash(splashViewModel.splash)
		else
			SplashLocalDataSource.requestSplash(splashViewModel.splash)
	}

	fun getSplash(): SplashResponse.Splash = SplashLocalDataSource.getSplash()
}