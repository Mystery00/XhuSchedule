package com.weilylab.xhuschedule.utils.userDo

import com.weilylab.xhuschedule.api.CourseAPI
import com.weilylab.xhuschedule.constant.ResponseCodeConstants
import com.weilylab.xhuschedule.constant.StringConstant
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.listener.DoSaveListener
import com.weilylab.xhuschedule.listener.RequestListener
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.response.CourseResponse
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
import vip.mystery0.rxpackagedata.rx.RxObservable
import vip.mystery0.rxpackagedata.rx.RxObserver
import java.util.*
import kotlin.collections.ArrayList

object CourseUtil {
	private const val RETRY_TIME = 1

	fun getCourse(student: Student, year: String?, term: String?, doSaveListener: DoSaveListener<List<Course>>?, requestListener: RequestListener<List<Course>>, index: Int = 0) {
		RetrofitFactory.retrofit
				.create(CourseAPI::class.java)
				.getCourses(student.username, year, term)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map {
					val courseResponse = GsonFactory.parse<CourseResponse>(it)
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
		val needRequestArray = ArrayList<Observable<DataWithUsername<CourseResponse>>>()
		resultArray.filter { !it }
				.forEachIndexed { position, _ ->
					needRequestArray.add(RetrofitFactory.retrofit
							.create(CourseAPI::class.java)
							.getCourses(studentList[position].username, year, term)
							.subscribeOn(Schedulers.newThread())
							.unsubscribeOn(Schedulers.newThread())
							.map {
								val courseResponse = GsonFactory.parse<CourseResponse>(it)
								if (courseResponse.rt == ResponseCodeConstants.DONE) {
									val saveMap = HashMap<String, List<Course>>()
									saveMap[studentList[position].username] = courseResponse.courses
									doSaveListener?.doSave(saveMap)
								}
								DataWithUsername(studentList[position].username, courseResponse)
							})
				}
		Observable.merge(needRequestArray)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<DataWithUsername<CourseResponse>> {
					override fun onComplete() {
						resumeRequest(resultArray, studentList, year, term, doSaveListener, map, doneListener, maxIndex, index)
					}

					override fun onSubscribe(d: Disposable) {
					}

					override fun onNext(t: DataWithUsername<CourseResponse>) {
						val username = t.username
						val data = t.data!!
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
		RxObservable<Boolean>()
				.doThings {
					Thread.sleep(200)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						request(resultArray, studentList, year, term, doSaveListener, map, doneListener, maxIndex, index + 1)
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
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
		RxObservable<ArrayList<Schedule>>()
				.doThings { observableEmitter ->
					val week = CalendarUtil.getWeekFromCalendar(InitLocalDataSource.getStartDateTime())
					val todayCourseList = ArrayList<Schedule>()
					val weekIndex = CalendarUtil.getWeekIndex()
					courseList.forEach {
						if (it.day == weekIndex && it.weekList.contains(week))
							todayCourseList.add(it)
					}
					todayCourseList.sortBy { it.start }
					observableEmitter.onFinish(todayCourseList)
				}
				.subscribe(object : RxObserver<ArrayList<Schedule>>() {
					override fun onFinish(data: ArrayList<Schedule>?) {
						listener.invoke(data!!)
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}

	fun convertCourseToSchedule(courseList: List<Course>): List<Schedule> {
		val list = ArrayList<Schedule>()
		courseList.forEach {
			list.add(it.schedule)
		}
		return list
	}

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

	internal class DataWithUsername<T>(val username: String, val data: T?)
}