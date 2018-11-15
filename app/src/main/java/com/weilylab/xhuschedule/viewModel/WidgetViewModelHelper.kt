package com.weilylab.xhuschedule.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.rxpackagedata.PackageData

object WidgetViewModelHelper {
	val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }
	val courseList by lazy { MediatorLiveData<PackageData<List<Schedule>>>() }
	val todayCourseList by lazy { MediatorLiveData<PackageData<List<Schedule>>>() }
	val testList by lazy { MediatorLiveData<PackageData<List<Test>>>() }
}