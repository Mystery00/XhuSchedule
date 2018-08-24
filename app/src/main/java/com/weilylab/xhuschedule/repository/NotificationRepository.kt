package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import com.zhuangfei.timetable.model.Schedule
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
}