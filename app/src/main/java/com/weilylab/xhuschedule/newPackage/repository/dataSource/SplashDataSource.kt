package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.SplashResponse

interface SplashDataSource {
	fun requestSplash(splashLiveData: MutableLiveData<SplashResponse.Splash>, requestResultLiveData: MutableLiveData<Int>)
}