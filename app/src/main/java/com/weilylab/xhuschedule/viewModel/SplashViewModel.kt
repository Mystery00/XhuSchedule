package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.response.SplashResponse
import vip.mystery0.rxpackagedata.PackageData

class SplashViewModel : ViewModel() {
	val splash by lazy { MediatorLiveData<PackageData<SplashResponse.Splash>>()}
}