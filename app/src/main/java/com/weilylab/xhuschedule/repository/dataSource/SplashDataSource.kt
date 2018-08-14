package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

interface SplashDataSource {
	fun requestSplash(splashPackageLiveData:MediatorLiveData<PackageData<SplashResponse.Splash>>)
}