package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
	val message = MutableLiveData<String>()
	val requestResult = MutableLiveData<Int>()
}