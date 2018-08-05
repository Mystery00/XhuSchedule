package com.weilylab.xhuschedule.newPackage.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.repository.dataSource.CourseDataSource
import com.weilylab.xhuschedule.newPackage.repository.local.service.impl.CourseServiceImpl
import com.weilylab.xhuschedule.newPackage.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.newPackage.utils.CourseUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs

object CourseLocalDataSource : CourseDataSource {
	private val courseService = CourseServiceImpl()

	override fun queryCourseByUsername(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, student: Student, year: String?, term: String?, isFromCache: Boolean) {
		RxObservable<List<Schedule>>()
				.doThings {
					try {
						if (year != null && term != null)
							it.onFinish(CourseUtil.convertCourseToSchedule(courseService.queryCourseByUsernameAndTerm(student.username, year, term)))
						else
							it.onFinish(CourseUtil.convertCourseToSchedule(courseService.queryCourseByUsernameAndTerm(student.username, "current", "current")))
					} catch (e: Exception) {
						it.onError(e)
					}
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						Logs.i("onFinish: ")
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache)
						} else {
							courseListLiveData.value = PackageData.error(e)
						}
					}
				})
	}

	fun deleteAllCourseListForStudent(username: String, year: String?, term: String?) {
		val list = courseService.queryCourseByUsernameAndTerm(username, year ?: "current", term
				?: "current")
		list.forEach {
			courseService.deleteCourse(it)
		}
	}

	fun saveCourseList(courseList: List<Course>) {
		courseList.forEach {
			courseService.addCourse(it)
		}
	}
}