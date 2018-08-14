package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

class QueryTestViewModel : ViewModel() {
	val studentList = MediatorLiveData<PackageData<List<Student>>>()
	val testList = MediatorLiveData<PackageData<List<Test>>>()
}