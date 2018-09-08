package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weilylab.xhuschedule.model.FeedBackMessage
import com.weilylab.xhuschedule.model.Notice
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.rxpackagedata.PackageData
import java.util.*

class BottomNavigationViewModel : ViewModel() {
	val studentList = MutableLiveData<PackageData<List<Student>>>()
	val studentInfo = MediatorLiveData<PackageData<StudentInfo>>()
	val courseList = MediatorLiveData<PackageData<List<Schedule>>>()
	val todayCourseList = MediatorLiveData<PackageData<List<Schedule>>>()
	val currentWeek = MediatorLiveData<PackageData<Int>>()

	val title = MutableLiveData<String>()
	val showCourse = MediatorLiveData<List<Schedule>>()
	val week = MutableLiveData<Int>()
	val startDateTime = MutableLiveData<PackageData<Calendar>>()

	val noticeList = MutableLiveData<PackageData<List<Notice>>>()

	val feedBackToken = MutableLiveData<PackageData<String>>()
	val newFeedBackMessageList = MutableLiveData<PackageData<List<FeedBackMessage>>>()

	val itemHeight = MutableLiveData<Int>()
}