package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.zhuangfei.timetable.model.Schedule

object WidgetViewModelHelper {
	val studentList = MutableLiveData<PackageData<List<Student>>>()
	val courseList = MediatorLiveData<PackageData<List<Schedule>>>()
	val todayCourseList = MediatorLiveData<PackageData<List<Schedule>>>()
}