package com.weilylab.xhuschedule.repository.local

import androidx.lifecycle.MutableLiveData
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.dataSource.CourseDataSource
import com.weilylab.xhuschedule.repository.local.service.impl.CourseServiceImpl
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.CourseUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.PackageData
import vip.mystery0.rxpackagedata.rx.DoNothingObserver
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver

object CourseLocalDataSource : CourseDataSource {
	private val courseService = CourseServiceImpl()

	override fun queryCourseByUsername(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, student: Student, year: String?, term: String?, isFromCache: Boolean, isShowError: Boolean) {
		RxObservable<List<Schedule>>()
				.doThings {
					it.onFinish(CourseUtil.convertCourseToSchedule(courseService.queryCourseByUsernameAndTerm(student.username, year
							?: "current", term ?: "current")))
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseByUsername(courseListLiveData, student, year, term, isFromCache, isShowError)
						} else {
							courseListLiveData.value = PackageData.error(e)
						}
					}
				})
	}

	override fun queryCourseWithManyStudent(courseListLiveData: MutableLiveData<PackageData<List<Schedule>>>, studentList: List<Student>, year: String?, term: String?, isFromCache: Boolean, isShowError: Boolean) {
		RxObservable<List<Schedule>>()
				.doThings { emitter ->
					val courses = ArrayList<Schedule>()
					studentList.forEach {
						courses.addAll(CourseUtil.convertCourseToSchedule(courseService.queryCourseByUsernameAndTerm(it.username, year
								?: "current", term ?: "current")))
					}
					emitter.onFinish(courses)
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						if (data == null || data.isEmpty())
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache, isShowError)
						else {
							courseListLiveData.value = PackageData.content(data)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						if (isFromCache) {
							CourseRemoteDataSource.queryCourseWithManyStudent(courseListLiveData, studentList, year, term, isFromCache, isShowError)
						} else {
							courseListLiveData.value = PackageData.error(e)
						}
					}
				})
	}

	fun queryDistinctCourseByUsernameAndTerm(courseListLiveData: MutableLiveData<PackageData<List<Course>>>) {
		courseListLiveData.value = PackageData.loading()
		RxObservable<List<Course>>()
				.doThings {
					it.onFinish(courseService.queryDistinctCourseByUsernameAndTerm())
				}
				.subscribe(object : RxObserver<List<Course>>() {
					override fun onFinish(data: List<Course>?) {
						if (data == null || data.isEmpty())
							courseListLiveData.value = PackageData.empty()
						else
							courseListLiveData.value = PackageData.content(data)
					}

					override fun onError(e: Throwable) {
						courseListLiveData.value = PackageData.error(e)
					}
				})
	}

	fun updateCourseColor(course: Course, color: String) {
		RxObservable<Boolean>()
				.doThings { observableEmitter ->
					val list = courseService.queryCourseByName(course.name)
					list.forEach {
						it.color = color
						courseService.updateCourse(it)
					}
					observableEmitter.onFinish(true)
				}
				.subscribe(DoNothingObserver<Boolean>())
	}

	fun getRowCourseList(student: Student, year: String, term: String): List<Schedule> = CourseUtil.convertCourseToSchedule(courseService.queryCourseByUsernameAndTerm(student.username, year, term))

	fun saveCourseList(username: String, year: String, term: String, courseList: List<Course>) {
		val savedList = courseService.queryCourseByUsernameAndTerm(username, year, term)
		savedList.forEach {
			courseService.deleteCourse(it)
		}
		courseList.forEach { course ->
			course.color = ""
			val has = savedList.find { it.name == course.name }
			if (has != null)
				course.color = has.color
			courseService.addCourse(course)
		}
		ConfigurationUtil.lastUpdateDate = CalendarUtil.getTodayDateString()
	}
}