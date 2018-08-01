package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.response.SplashResponse
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

class SplashViewModel : ViewModel() {
	val splash = MediatorLiveData<PackageData<SplashResponse.Splash>>()
}