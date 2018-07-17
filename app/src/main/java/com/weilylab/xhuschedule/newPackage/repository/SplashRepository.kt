package com.weilylab.xhuschedule.newPackage.repository

import com.weilylab.xhuschedule.newPackage.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.newPackage.repository.remote.SplashRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.NetworkUtil
import com.weilylab.xhuschedule.newPackage.viewModel.SplashViewModel

class SplashRepository {
	companion object {
		const val DONE = 21
		const val ERROR = 22
	}

	fun requestSplash(splashViewModel: SplashViewModel) {
		if (NetworkUtil.isConnectInternet())
			SplashRemoteDataSource.requestSplash(splashViewModel.splash, splashViewModel.requestResult)
		else
			SplashLocalDataSource.requestSplash(splashViewModel.splash, splashViewModel.requestResult)
	}
}