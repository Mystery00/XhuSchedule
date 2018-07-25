package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.Test

class QueryTestViewModel : ViewModel() {
	val studentList = MutableLiveData<List<Student>>()
	val testList = MutableLiveData<List<Test>>()
	val message = MutableLiveData<String>()
	val requestCode = MutableLiveData<Int>()
}