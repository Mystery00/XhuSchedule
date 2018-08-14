package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.CourseDataSource
import com.weilylab.xhuschedule.repository.local.service.impl.CourseServiceImpl
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.utils.CourseUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
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

	override fun queryCourseWithManyStudent(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, studentList: List<Student>, year: String?, term: String?, isFromCache: Boolean) {
		RxObservable<List<Schedule>>()
				.doThings { emitter ->
					try {
						val courses = ArrayList<Schedule>()
						if (year != null && term != null)
							studentList.forEach {
								courses.addAll(CourseUtil.convertCourseToSchedule(courseService.queryCourseByUsernameAndTerm(it.username, year, term)))
							}
						else
							studentList.forEach {
								courses.addAll(CourseUtil.convertCourseToSchedule(courseService.queryCourseByUsernameAndTerm(it.username, "current", "current")))
							}
						emitter.onFinish(courses)
					} catch (e: Exception) {
						emitter.onError(e)
					}
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						Logs.i("onFinish: ")
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache)
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