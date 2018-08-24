package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.repository.local.TestLocalDataSource
import com.weilylab.xhuschedule.repository.remote.CourseRemoteDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.CourseUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import java.util.ArrayList

object NotificationRepository {
	fun queryTomorrowCourseByUsername(student: Student, year: String?, term: String?, listener: (PackageData<List<Schedule>>) -> Unit) {
		listener.invoke(PackageData.loading())
		RxObservable<List<Schedule>>()
				.doThings { observableEmitter ->
					val startTime = InitLocalDataSource.getStartDataTime()
					val currentWeek = CalendarUtil.getWeekFromCalendar(startTime)
					val tomorrow = CalendarUtil.getTomorrowIndex()
					val courseList = CourseLocalDataSource.getRowCourseList(student, year
							?: "current", term ?: "current")
					val tomorrowCourseList = ArrayList<Schedule>()
					tomorrowCourseList.addAll(courseList.filter { it.weekList.contains(currentWeek) && it.day == tomorrow })
					observableEmitter.onFinish(tomorrowCourseList)
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						listener.invoke(PackageData.content(data))
					}

					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}
				})
	}

	fun queryTomorrowCourseForManyStudent(studentList: List<Student>, year: String?, term: String?, listener: (PackageData<List<Schedule>>) -> Unit) {
		listener.invoke(PackageData.loading())
		RxObservable<List<Schedule>>()
				.doThings { emitter ->
					val startTime = InitLocalDataSource.getStartDataTime()
					val currentWeek = CalendarUtil.getWeekFromCalendar(startTime)
					val tomorrow = CalendarUtil.getTomorrowIndex()
					val courseList = ArrayList<Schedule>()
					studentList.forEach {
						courseList.addAll(CourseLocalDataSource.getRowCourseList(it, year
								?: "current", term ?: "current"))
					}
					val tomorrowCourseList = ArrayList<Schedule>()
					tomorrowCourseList.addAll(courseList.filter { it.weekList.contains(currentWeek) && it.day == tomorrow })
					emitter.onFinish(courseList)
				}
				.subscribe(object : RxObserver<List<Schedule>>() {
					override fun onFinish(data: List<Schedule>?) {
						listener.invoke(PackageData.content(data))
					}

					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}
				})
	}

	fun queryTomorrowTestByUsername(student: Student, listener: (PackageData<List<Test>>) -> Unit) {
		listener.invoke(PackageData.loading())
		RxObservable<List<Test>>()
				.doThings { emitter ->
					val courseList = TestLocalDataSource.getRawTestList(student)
					val tomorrowCourseList = ArrayList<Test>()
					tomorrowCourseList.addAll(courseList.filter { CalendarUtil.isTomorrowTest(it.date) })
					emitter.onFinish(courseList)
				}
				.subscribe(object : RxObserver<List<Test>>() {
					override fun onFinish(data: List<Test>?) {
						listener.invoke(PackageData.content(data))
					}

					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}
				})

	}

	fun queryTomorrowTestForManyStudent(studentList: List<Student>, listener: (PackageData<List<Test>>) -> Unit) {
		listener.invoke(PackageData.loading())
		RxObservable<List<Test>>()
				.doThings { emitter ->
					val courseList = ArrayList<Test>()
					studentList.forEach {
						courseList.addAll(TestLocalDataSource.getRawTestList(it))
					}
					val tomorrowCourseList = ArrayList<Test>()
					tomorrowCourseList.addAll(courseList.filter { CalendarUtil.isTomorrowTest(it.date) })
					emitter.onFinish(courseList)
				}
				.subscribe(object : RxObserver<List<Test>>() {
					override fun onFinish(data: List<Test>?) {
						listener.invoke(PackageData.content(data))
					}

					override fun onError(e: Throwable) {
						listener.invoke(PackageData.error(e))
					}
				})
	}
}