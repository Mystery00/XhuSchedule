package com.weilylab.xhuschedule.newPackage.repository.dataSource

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student

interface CourseDataSource {
	fun queryCourseByUsername(courseListLiveData: MutableLiveData<List<Course>>, todayCourseListLiveData: MutableLiveData<List<Course>>, messageLiveData: MutableLiveData<String>, requestCodeLiveData: MutableLiveData<Int>, student: Student, year: String?, term: String?, isFromCache: Boolean)
}