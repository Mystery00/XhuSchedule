package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import vip.mystery0.rxpackagedata.PackageData

class QueryTestViewModel : ViewModel() {
	val studentList by lazy { MediatorLiveData<PackageData<List<Student>>>() }
	val testList by lazy { MediatorLiveData<PackageData<List<Test>>>() }
}