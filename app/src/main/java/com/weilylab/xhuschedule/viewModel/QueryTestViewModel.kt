package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import vip.mystery0.rxpackagedata.PackageData

class QueryTestViewModel : ViewModel() {
	val studentList by lazy { MediatorLiveData<PackageData<List<Student>>>() }
	val student by lazy { MutableLiveData<String>() }
	val testList by lazy { MediatorLiveData<PackageData<List<Test>>>() }
	val html by lazy { MutableLiveData<String>() }
}