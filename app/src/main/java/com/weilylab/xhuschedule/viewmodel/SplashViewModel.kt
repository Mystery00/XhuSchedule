package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Splash
import vip.mystery0.rx.PackageData
import vip.mystery0.rx.launch

class SplashViewModel : ViewModel() {
	val splash by lazy { MediatorLiveData<PackageData<Splash>>() }

	fun requestSplash(){
		launch(splash){
			if ()
		}
	}
}