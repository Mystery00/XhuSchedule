package com.weilylab.xhuschedule.newPackage.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.StudentInfo
import java.util.*

class BottomNavigationViewModel : ViewModel() {
	val studentList = MutableLiveData<List<Student>>()
	val studentInfo = MutableLiveData<StudentInfo>()
	val courseList = MutableLiveData<List<Course>>()
	val todayCourseList = MutableLiveData<List<Course>>()
	val week = MutableLiveData<Int>()
	val currentWeek = MutableLiveData<Int>()
	val startDateTime = MutableLiveData<Calendar>()
	val message = MutableLiveData<String>()
	val requestCode = MutableLiveData<Int>()
	val action = MutableLiveData<Int>()
	val showCourse = MutableLiveData<Course>()
}