package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

class SplashViewModel : ViewModel() {
	val splash = MediatorLiveData<PackageData<SplashResponse.Splash>>()
}