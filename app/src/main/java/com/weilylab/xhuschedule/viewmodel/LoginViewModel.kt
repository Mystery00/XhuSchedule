package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import vip.mystery0.rxpackagedata.PackageData

class LoginViewModel : ViewModel() {
	val loginLiveData by lazy { MutableLiveData<PackageData<Student>>() }
}