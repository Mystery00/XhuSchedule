package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import com.weilylab.xhuschedule.repository.remote.SplashRemoteDataSource
import com.weilylab.xhuschedule.viewmodel.SplashViewModel
import vip.mystery0.rx.DataManager
import vip.mystery0.tools.utils.NetworkTools

object SplashRepository {
	fun requestSplash(splashViewModel: SplashViewModel) {
		DataManager.instance().doRequest(splashViewModel.splash) {
			if (NetworkTools.instance.isConnectInternet())
				SplashRemoteDataSource.requestSplash(splashViewModel.splash)
			else
				SplashLocalDataSource.requestSplash(splashViewModel.splash)
		}
	}

	fun getSplash(): Splash = SplashLocalDataSource.getSplash()
}