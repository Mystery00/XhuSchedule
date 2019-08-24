package com.weilylab.xhuschedule.repository.ds

import androidx.lifecycle.MediatorLiveData
import com.weilylab.xhuschedule.model.Splash
import vip.mystery0.rx.PackageData

interface SplashDataSource {
	fun requestSplash(splashPackageLiveData: MediatorLiveData<PackageData<Splash>>)
}