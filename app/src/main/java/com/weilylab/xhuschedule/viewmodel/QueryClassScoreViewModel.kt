package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.ClassScore
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import vip.mystery0.rxpackagedata.PackageData

class QueryClassScoreViewModel : ViewModel() {
	val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }
	val studentInfoList by lazy { MediatorLiveData<PackageData<Map<Student, StudentInfo?>>>() }
	val scoreList by lazy { MutableLiveData<PackageData<List<ClassScore>>>() }
	val student by lazy { MutableLiveData<Student>() }
	val year by lazy { MutableLiveData<String>() }
	val term by lazy { MutableLiveData<String>() }
}