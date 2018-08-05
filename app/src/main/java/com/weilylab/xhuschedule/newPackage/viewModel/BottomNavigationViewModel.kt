package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.zhuangfei.timetable.model.Schedule
import java.util.*

class BottomNavigationViewModel : ViewModel() {
	val studentList = MutableLiveData<PackageData<List<Student>>>()
	val studentInfo = MediatorLiveData<PackageData<StudentInfo>>()
	val courseList = MediatorLiveData<PackageData<List<Schedule>>>()
	val todayCourseList = MediatorLiveData<PackageData<List<Schedule>>>()
	val currentWeek = MediatorLiveData<PackageData<Int>>()
	val startDateTime = MutableLiveData<PackageData<Calendar>>()

	val title = MutableLiveData<String>()
	val showCourse = MediatorLiveData<List<Schedule>>()
	val week = MutableLiveData<Int>()
}