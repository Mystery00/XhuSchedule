package com.weilylab.xhuschedule.repository.remote

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.ds.SplashDataSource
import com.weilylab.xhuschedule.repository.local.SplashLocalDataSource
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.error
import vip.mystery0.rx.loading

object SplashRemoteDataSource : SplashDataSource {
	override fun requestSplash(splashPackageLiveData: MediatorLiveData<PackageData<Splash>>) {
		splashPackageLiveData.loading()
		val response = RetrofitFactory.retrofit.create(XhuScheduleCloudAPI::class.java)
				.requestSplashInfo()
				.execute()
		if (response.isSuccessful) {
			val cloudResponse = response.body()!!
			if (cloudResponse.code == 0) {
				val splash = cloudResponse.data
				if (splash == null || !splash.enable) {
					SplashLocalDataSource.removeSplash()
				} else {
					SplashLocalDataSource.saveSplash(splash)
				}
			} else {
				splashPackageLiveData.error(Exception(cloudResponse.message))
			}
		} else {
			splashPackageLiveData.error(Exception(response.errorBody()?.string()))
		}
	}
}