package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import com.zhuangfei.timetable.model.Schedule
import java.util.*

class BottomNavigationViewModel : ViewModel() {
	val studentList = MutableLiveData<List<Student>>()
	val studentInfo = MutableLiveData<StudentInfo>()
	val courseList = MutableLiveData<List<Schedule>>()
	val todayCourseList = MutableLiveData<List<Schedule>>()
	val week = MutableLiveData<Int>()
	val currentWeek = MutableLiveData<Int>()
	val startDateTime = MutableLiveData<Calendar>()
	val message = MutableLiveData<String>()
	val requestCode = MutableLiveData<Int>()
	val action = MutableLiveData<Int>()
	val showCourse = MutableLiveData<List<Schedule>>()
	val title = MutableLiveData<String>()
}