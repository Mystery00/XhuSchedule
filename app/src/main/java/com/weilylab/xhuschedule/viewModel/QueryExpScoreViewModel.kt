package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.ExpScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData

class QueryExpScoreViewModel:ViewModel() {
	val studentList = MutableLiveData<PackageData<List<Student>>>()
	val studentInfoList = MediatorLiveData<PackageData<Map<Student, StudentInfo?>>>()
	val scoreList = MutableLiveData<PackageData<List<ExpScore>>>()
	val student = MutableLiveData<Student>()
	val year = MutableLiveData<String>()
	val term = MutableLiveData<String>()
}