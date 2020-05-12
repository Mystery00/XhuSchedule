package com.weilylab.xhuschedule.repository.remote

import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.ds.SplashDataSource
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource

class SplashRemoteDataSource(
		private val xhuScheduleCloudAPI: XhuScheduleCloudAPI,
		private val splashLocalDataSource: SplashLocalDataSource
) : SplashDataSource {
	override suspend fun requestSplash(): Splash {
		val splashResponse = xhuScheduleCloudAPI.requestSplashInfo()
		if (splashResponse.data == null || !splashResponse.data!!.enable)
			splashLocalDataSource.removeSplash()
		else
			splashLocalDataSource.saveSplash(splashResponse.data!!)
		return splashLocalDataSource.requestSplash()
	}
}