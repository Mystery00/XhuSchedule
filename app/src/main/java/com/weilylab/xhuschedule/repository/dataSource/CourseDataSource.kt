package com.weilylab.xhuschedule.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.zhuangfei.timetable.model.Schedule

interface CourseDataSource {
	fun queryCourseByUsername(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, student: Student, year: String?, term: String?, isFromCache: Boolean)

	fun queryCourseWithManyStudent(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, studentList: List<Student>, year: String?, term: String?, isFromCache: Boolean)
}