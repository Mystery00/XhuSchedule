package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

class LoginViewModel : ViewModel() {
	val loginLiveData = MutableLiveData<PackageData<Student>>()
}