package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.model.response.SplashResponse
import vip.mystery0.rxpackagedata.PackageData

interface SplashDataSource {
	fun requestSplash(splashPackageLiveData:MediatorLiveData<PackageData<SplashResponse.Splash>>)
}