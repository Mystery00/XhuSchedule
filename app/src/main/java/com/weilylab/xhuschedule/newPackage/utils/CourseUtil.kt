package com.weilylab.xhuschedule.newPackage.utils

import com.weilylab.xhuschedule.newPackage.api.CourseAPI
import com.weilylab.xhuschedule.newPackage.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.newPackage.constant.StringConstant
import com.weilylab.xhuschedule.newPackage.factory.GsonFactory
import com.weilylab.xhuschedule.newPackage.factory.RetrofitFactory
import com.weilylab.xhuschedule.newPackage.listener.DoSaveListener
import com.weilylab.xhuschedule.newPackage.listener.RequestListener
import com.weilylab.xhuschedule.newPackage.model.Course
import com.weilylab.xhuschedule.newPackage.model.Student
import com.weilylab.xhuschedule.newPackage.model.response.CourseResponse
import com.weilylab.xhuschedule.newPackage.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.zhuangfei.timetable.model.Schedule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import java.util.ArrayList

object CourseUtil {
	private const val RETRY_TIME = 1

	fun getCourse(student: Student, year: String?, term: String?, doSaveListener: DoSaveListener<List<Course>>?, requestListener: RequestListener<List<Course>>, index: Int = 0) {
		RetrofitFactory.tomcatRetrofit
				.create(CourseAPI::class.java)
				.getCourses(student.username, year, term)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val courseResponse = GsonFactory.parseInputStream(it.byteStream(), CourseResponse::class.java)
					if (courseResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(courseResponse.courses)
					courseResponse
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<CourseResponse>() {
					override fun onFinish(data: CourseResponse?) {
						when {
							data == null -> requestListener.error(ResponseCodeConstants.UNKNOWN_ERROR, StringConstant.hint_data_null)
							data.rt == ResponseCodeConstants.DONE -> requestListener.done(data.courses)
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									requestListener.error(ResponseCodeConstants.DO_TOO_MANY, StringConstant.hint_do_too_many)
								else
									UserUtil.login(student, null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											getCourse(student, year, term, doSaveListener, requestListener, index + 1)
										}

										override fun error(rt: String, msg: String?) {
											requestListener.error(rt, msg)
										}
									})
							}
							else -> requestListener.error(data.rt, data.msg)
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
					}
				})
	}

	fun getTodayCourse(courseList: List<Schedule>, listener: (List<Schedule>) -> Unit) {
		val week = CalendarUtil.getWeekFromCalendar(InitLocalDataSource.getStartDataTime())
		val todayCourseList = ArrayList<Schedule>()
		val weekIndex = CalendarUtil.getWeekIndex()
		courseList.forEach {
			if (it.day == weekIndex&&it.weekList.contains(week)) {
					todayCourseList.add(it)
			}
		}
		listener.invoke(todayCourseList)
	}

	fun convertCourseToSchedule(courseList: List<Course>): List<Schedule> {
		val list = ArrayList<Schedule>()
		courseList.forEach {
			list.add(it.schedule)
		}
		return filterCourse(list)
	}

	private fun filterCourse(courseList: List<Schedule>): List<Schedule> {
		val list = ArrayList<Schedule>()
		courseList.forEach {
			if (check(list, it))
				list.add(it)
		}
		return list
	}

	private fun check(courseList: List<Schedule>, schedule: Schedule): Boolean {
		courseList.forEach {
			if (it.name == schedule.name &&
					it.room == schedule.room &&
					it.teacher == schedule.teacher &&
					it.start == schedule.start &&
					it.step == schedule.step &&
					it.day == schedule.day) {
				it.weekList.addAll(schedule.weekList)
				return false
			}
		}
		return true
	}
}