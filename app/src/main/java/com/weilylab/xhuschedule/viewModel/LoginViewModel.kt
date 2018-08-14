package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

class LoginViewModel : ViewModel() {
	val loginLiveData = MutableLiveData<PackageData<Student>>()
}