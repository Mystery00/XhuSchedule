package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.response.SplashResponse

class SplashViewModel : ViewModel() {
	val splash = MutableLiveData<SplashResponse.Splash>()
	val requestResult = MutableLiveData<Int>()
}