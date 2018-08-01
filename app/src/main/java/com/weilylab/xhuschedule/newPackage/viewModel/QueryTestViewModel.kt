package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData

class QueryTestViewModel : ViewModel() {
	val studentList = MediatorLiveData<PackageData<List<Student>>>()
	val testList = MediatorLiveData<PackageData<List<Test>>>()
}