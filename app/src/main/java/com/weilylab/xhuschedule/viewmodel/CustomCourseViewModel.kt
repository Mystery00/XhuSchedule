package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import vip.mystery0.rx.PackageData

class CustomCourseViewModel : ViewModel() {
	val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }
	val studentInfoList by lazy { MediatorLiveData<PackageData<Map<Student, StudentInfo?>>>() }
	val customCourseList by lazy { MutableLiveData<PackageData<List<Any>>>() }
	val syncCustomCourse by lazy { MutableLiveData<PackageData<Boolean>>() }
	val time by lazy { MutableLiveData<Pair<Int, Int>>() }
	val weekIndex by lazy { MutableLiveData<Int>() }
	val student by lazy { MutableLiveData<Student>() }
	val year by lazy { MutableLiveData<String>() }
	val term by lazy { MutableLiveData<String>() }
}