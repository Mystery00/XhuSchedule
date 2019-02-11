package com.weilylab.xhuschedule.viewmodel

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
	val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }
	val studentInfo by lazy { MediatorLiveData<PackageData<StudentInfo>>() }
	val courseList by lazy { MediatorLiveData<PackageData<List<Schedule>>>() }
	val todayCourseList by lazy { MediatorLiveData<PackageData<List<Schedule>>>() }
	val currentWeek by lazy { MediatorLiveData<PackageData<Int>>() }

	val title by lazy { MutableLiveData<String>() }
	val showCourse by lazy { MediatorLiveData<List<Schedule>>() }
	val week by lazy { MutableLiveData<Int>() }
	val startDateTime by lazy { MutableLiveData<PackageData<Calendar>>() }

	val noticeList by lazy { MutableLiveData<PackageData<List<Notice>>>() }

	val feedBackToken by lazy { MutableLiveData<PackageData<String>>() }
	val newFeedBackMessageList by lazy { MutableLiveData<PackageData<List<FeedBackMessage>>>() }

	val itemHeight by lazy { MutableLiveData<Int>() }
}