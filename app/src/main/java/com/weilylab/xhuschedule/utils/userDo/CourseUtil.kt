package com.weilylab.xhuschedule.utils.userDo

import com.weilylab.xhuschedule.api.CourseAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.factory.fromJson
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.CourseResponse
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.model.Schedule
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver
import java.util.*
import kotlin.collections.ArrayList

object CourseUtil {
	private const val RETRY_TIME = 1

	fun getCourse(student: Student, year: String?, term: String?, doSaveListener: DoSaveListener<List<Course>>?, requestListener: RequestListener<List<Course>>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(CourseAPI::class.java)
				.getCourses(student.username, year, term)
				.subscribeOn(Schedulers.io())
				.map {
					val courseResponse = it.fromJson<CourseResponse>()
					if (courseResponse.rt == ResponseCodeConstants.DONE)
						doSaveListener?.doSave(courseResponse.courses)
					courseResponse
				}
				.doOnNext {
					if (it.rt == ResponseCodeConstants.DONE)
						it.courses.addAll(CourseLocalDataSource.getCustomCourseList(student, year, term))
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<CourseResponse>() {
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

	fun getCoursesForManyStudent(studentList: List<Student>, year: String?, term: String?, doSaveListener: DoSaveListener<Map<String, List<Course>>>?, requestListener: RequestListener<List<Course>>) {
		try {
			val maxIndex = 2
			val resultArray = BooleanArray(studentList.size) { false }
			val map = HashMap<String, List<Course>>()
			request(resultArray, studentList, year, term, doSaveListener, map, {
				val courseList = ArrayList<Course>()
				map.keys.forEach {
					courseList.addAll(map[it]!!)
				}
				requestListener.done(courseList)
			}, maxIndex)
		} catch (e: Exception) {
			requestListener.error(ResponseCodeConstants.CATCH_ERROR, e.message)
		}
	}

	private fun request(resultArray: BooleanArray, studentList: List<Student>, year: String?, term: String?, doSaveListener: DoSaveListener<Map<String, List<Course>>>?, map: HashMap<String, List<Course>>, doneListener: () -> Unit, maxIndex: Int, index: Int = 0) {
		if (index >= maxIndex || isAllDone(resultArray)) {
			doneListener.invoke()
			return
		}
		val needRequestArray = ArrayList<Observable<Pair<String, CourseResponse>>>()
		resultArray.filter { !it }
				.forEachIndexed { position, _ ->
					needRequestArray.add(RetrofitFactory.retrofit
							.create(CourseAPI::class.java)
							.getCourses(studentList[position].username, year, term)
							.subscribeOn(Schedulers.io())
							.map {
								val courseResponse = it.fromJson<CourseResponse>()
								if (courseResponse.rt == ResponseCodeConstants.DONE) {
									val saveMap = HashMap<String, List<Course>>()
									saveMap[studentList[position].username] = courseResponse.courses
									doSaveListener?.doSave(saveMap)
								}
								Pair(studentList[position].username, courseResponse)
							}.doOnNext {
								val response = it.second
								if (response.rt == ResponseCodeConstants.DONE)
									response.courses.addAll(CourseLocalDataSource.getCustomCourseListByUsername(it.first, year, term))
							})

				}
		Observable.merge(needRequestArray)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<Pair<String, CourseResponse>> {
					override fun onComplete() {
						resumeRequest(resultArray, studentList, year, term, doSaveListener, map, doneListener, maxIndex, index)
					}

					override fun onSubscribe(d: Disposable) {
					}

					override fun onNext(t: Pair<String, CourseResponse>) {
						val username = t.first
						val data = t.second
						val position = studentList.indexOfFirst { it.username == username }
						when {
							data.rt == ResponseCodeConstants.DONE -> {
								map[username] = data.courses
								resultArray[position] = true
							}
							data.rt == ResponseCodeConstants.ERROR_NOT_LOGIN -> {
								if (index == RETRY_TIME)
									resultArray[position] = false
								else
									UserUtil.login(studentList[position], null, object : RequestListener<Boolean> {
										override fun done(t: Boolean) {
											resultArray[position] = false
										}

										override fun error(rt: String, msg: String?) {
											Logs.em("error: ", rt, msg)
											resultArray[position] = false
										}
									})
							}
							else -> resultArray[position] = false
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						resumeRequest(resultArray, studentList, year, term, doSaveListener, map, doneListener, maxIndex, index)
					}
				})
	}

	private fun resumeRequest(resultArray: BooleanArray, studentList: List<Student>, year: String?, term: String?, doSaveListener: DoSaveListener<Map<String, List<Course>>>?, map: HashMap<String, List<Course>>, doneListener: () -> Unit, maxIndex: Int, index: Int) {
		Observable.create<Boolean> {
			Thread.sleep(200)
			it.onComplete()
		}
				.subscribeOn(Schedulers.single())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}

					override fun onFinish(data: Boolean?) {
						request(resultArray, studentList, year, term, doSaveListener, map, doneListener, maxIndex, index + 1)
					}
				})
	}

	private fun isAllDone(resultArray: BooleanArray): Boolean {
		resultArray.forEach {
			if (!it)
				return false
		}
		return true
	}

	fun getTodayCourse(courseList: List<Schedule>, listener: (List<Schedule>) -> Unit) {
		Observable.create<Pair<Int, Int>> {
			val shouldShowTomorrow = CalendarUtil.shouldShowTomorrowInfo()
			val week = if (shouldShowTomorrow) CalendarUtil.getTomorrowWeekFromCalendar(InitLocalDataSource.getStartDateTime())
			else CalendarUtil.getWeekFromCalendar(InitLocalDataSource.getStartDateTime())
			val weekIndex = if (shouldShowTomorrow) CalendarUtil.getTomorrowIndex()
			else CalendarUtil.getWeekIndex()
			it.onNext(Pair(week, weekIndex))
			it.onComplete()
		}
				.subscribeOn(Schedulers.computation())
				.observeOn(Schedulers.io())
				.map { pair ->
					val todayCourseList = ArrayList<Schedule>()
					todayCourseList.addAll(courseList.filter { isTodayCourse(it, pair.first, pair.second) }
							.sortedBy { it.start })
					todayCourseList
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<List<Schedule>>() {
					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}

					override fun onFinish(data: List<Schedule>?) {
						listener.invoke(data!!)
					}
				})
	}

	fun splitWeekString(list: List<Int>): String {
		val stringBuilder = StringBuilder()
		list.forEachIndexed f@{ index, i ->
			when (index) {
				0 -> stringBuilder.append(i)
				list.size - 1 -> {
					if (list[index] - list[index - 1] == 1) stringBuilder.append("-").append(list[index])
					else stringBuilder.append(",").append(list[index])
				}
				else -> {
					if ((list[index] - list[index - 1] == 1) && (list[index + 1] - list[index] == 1))
						return@f
					if ((list[index] - list[index - 1] == 1) && (list[index + 1] - list[index] != 1))
						stringBuilder.append("-").append(list[index])
					if (list[index] - list[index - 1] != 1)
						stringBuilder.append(",").append(list[index])
				}
			}
		}
		return stringBuilder.toString()
	}

	fun isTodayCourse(schedule: Schedule, week: Int, weekIndex: Int): Boolean = schedule.day == weekIndex && schedule.weekList.contains(week)

	fun convertCourseToSchedule(courseList: List<Course>): List<Schedule> = courseList.map { it.schedule }

	fun filterShowCourse(courseList: List<Schedule>, week: Int): List<Schedule> {
		if (ConfigurationUtil.isShowNotWeek)
			return courseList
		val list = ArrayList<Schedule>()
		courseList.forEach {
			if (it.weekList.contains(week))
				list.add(it)
		}
		return list
	}
}