package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.newPackage.model.response.SplashResponse
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

interface SplashDataSource {
	fun requestSplash(splashPackageLiveData:MediatorLiveData<PackageData<SplashResponse.Splash>>)
}