package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.zhuangfei.timetable.model.Schedule

interface CourseDataSource {
	fun queryCourseByUsername(courseListLiveData: MutableLiveData<List<Schedule>>, todayCourseListLiveData: MutableLiveData<List<Schedule>>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student, year: String?, term: String?, isFromCache: Boolean)
}