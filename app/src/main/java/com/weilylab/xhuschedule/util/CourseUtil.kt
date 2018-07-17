/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import android.util.Base64
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.LoginActivity
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.CourseTimeInfo
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.classes.rt.AutoLoginRT
import com.weilylab.xhuschedule.classes.rt.GetCourseRT
import com.weilylab.xhuschedule.interfaces.StudentService
import com.weilylab.xhuschedule.interfaces.UserService
import com.weilylab.xhuschedule.listener.GetCourseListener
import com.weilylab.xhuschedule.listener.LoginListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_main.*
import vip.mystery0.logs.Logs
import java.io.File
import java.io.InputStreamReader
import java.net.UnknownHostException
import kotlin.collections.ArrayList

/**
 * Created by myste.
 */
object CourseUtil {
	fun getCoursesFromFile(file: File): Array<Course> {
		try {
			if (!file.exists())
				return emptyArray()
			val courses = XhuFileUtil.getArrayFromFile(file, Course::class.java)
			addColor(courses)
			return courses
		} catch (e: Exception) {
			e.printStackTrace()
			return emptyArray()
		}
	}

	fun setColor(nameWithOutMD5: String, color: Int) {
		val md5 = ScheduleHelper.getMD5(nameWithOutMD5)
		ColorUtil.saveColor(md5, color)
	}

	fun getColor(nameWithOutMD5: String): Int {
		val md5 = ScheduleHelper.getMD5(nameWithOutMD5)
		return ColorUtil.getCourseColor(md5)
	}

	fun getColor(course: Course): Int {
		return getColor(course.name)
	}

	fun addColor(courseArray: Array<Course>) {
		return addColor(courseArray.toList())
	}

	fun addColor(courses: List<Course>) {
		courses.forEach {
			addColor(it)
		}
	}

	fun addColor(course: Course) {
		val md5 = ScheduleHelper.getMD5(course.name)
		val color = ColorUtil.getCourseColor(md5)
		course.color = color
	}

	fun getAllCourses(courses: Array<Course>): ArrayList<ArrayList<ArrayList<Course>>> {
		val array = Array(11, { Array<ArrayList<Course>>(7, { ArrayList() }) })
		courses.forEach {
			try {//尝试解析时间
				val timeArray = it.time.split('-')
				val startTime = timeArray[0].toInt() - 1
				var flag = false
				for (temp in array[startTime][it.day.toInt() - 1]) {
					flag = temp.with(it)
					if (flag)
						break
				}
				if (!flag)
					array[startTime][it.day.toInt() - 1].add(it)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
		val list = ArrayList<ArrayList<ArrayList<Course>>>()
		for (i in 0 until array.size) {
			list.add(ArrayList())
			for (k in 0 until array[i].size)
				list[i].add(array[i][k])
		}
		return list
	}

	fun formatCourses(courses: Array<Course>): ArrayList<ArrayList<ArrayList<Course>>> {
		val firstWeekOfTerm = Settings.firstWeekOfTerm
		val date = firstWeekOfTerm.split('-')
		CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
		val currentWeek = CalendarUtil.getWeek()
		ScheduleHelper.weekIndex = currentWeek
		return formatCourses(courses, currentWeek)
	}

	fun formatCourses(courses: Array<Course>, weekIndex: Int): ArrayList<ArrayList<ArrayList<Course>>> {
		val array = Array(11, { Array<ArrayList<Course>>(7, { ArrayList() }) })
		courses.forEach {
			try {//尝试解析周数
				var other = false
				when (it.type) {
					Constants.COURSE_TYPE_ERROR, Constants.COURSE_TYPE_ALL -> other = true
					Constants.COURSE_TYPE_SINGLE -> if (weekIndex % 2 == 1)
						other = true
					Constants.COURSE_TYPE_DOUBLE -> if (weekIndex % 2 == 0)
						other = true
					else -> other = false
				}
				val weekArray = it.week.split('-')
				val startWeek = weekArray[0].toInt()
				val endWeek = weekArray[1].toInt()
				if ((weekIndex !in startWeek..endWeek) || !other)
					it.type = Constants.COURSE_TYPE_NOT
			} catch (e: Exception) {
				e.printStackTrace()
				ScheduleHelper.isAnalysisError = true
			}
			try {//尝试解析时间
				val timeArray = it.time.split('-')
				val startTime = timeArray[0].toInt() - 1
				var flag = false
				for (temp in array[startTime][it.day.toInt() - 1]) {
					flag = temp.with(it)
					if (flag)
						break
				}
				if (!flag)
					array[startTime][it.day.toInt() - 1].add(it)
			} catch (e: Exception) {
				e.printStackTrace()
				ScheduleHelper.isAnalysisError = true
			}
		}
		val list = ArrayList<ArrayList<ArrayList<Course>>>()
		for (i in 0 until array.size) {
			list.add(ArrayList())
			for (k in 0 until array[i].size)
				list[i].add(array[i][k])
		}
		return list
	}

	fun getWeekCourses(courses: Array<Course>): ArrayList<ArrayList<ArrayList<Course>>> {
		val firstWeekOfTerm = Settings.firstWeekOfTerm
		val date = firstWeekOfTerm.split('-')
		CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
		val currentWeek = CalendarUtil.getWeek()
		ScheduleHelper.weekIndex = currentWeek
		return getWeekCourses(courses, currentWeek)
	}

	fun getWeekCourses(courses: Array<Course>, weekIndex: Int): ArrayList<ArrayList<ArrayList<Course>>> {
		ScheduleHelper.weekIndex = weekIndex
		val array = Array(11, { Array<ArrayList<Course>>(7, { ArrayList() }) })
		courses.filter {
			try {
				var other = false
				when (it.type) {
					Constants.COURSE_TYPE_ERROR, Constants.COURSE_TYPE_ALL -> other = true
					Constants.COURSE_TYPE_SINGLE -> if (weekIndex % 2 == 1)
						other = true
					Constants.COURSE_TYPE_DOUBLE -> if (weekIndex % 2 == 0)
						other = true
					else -> other = false
				}
				val weekArray = it.week.split('-')
				val startWeek = weekArray[0].toInt()
				val endWeek = weekArray[1].toInt()
				weekIndex in startWeek..endWeek && other
			} catch (e: Exception) {
				false
			}
		}.forEach {
			val timeArray = it.time.split('-')
			val startTime = timeArray[0].toInt() - 1
			var flag = false
			for (temp in array[startTime][it.day.toInt() - 1]) {
				flag = temp.with(it)
				if (flag)
					break
			}
			if (!flag)
				array[startTime][it.day.toInt() - 1].add(it)
		}
		val list = ArrayList<ArrayList<ArrayList<Course>>>()
		for (i in 0 until array.size) {
			list.add(ArrayList())
			for (k in 0 until array[i].size)
				list[i].add(array[i][k])
		}
		return list
	}

	fun getTodayCourses(courses: Array<Course>): ArrayList<Course> {
		val firstWeekOfTerm = Settings.firstWeekOfTerm
		val date = firstWeekOfTerm.split('-')
		CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
		//获取当前第几周
		val currentWeek = CalendarUtil.getWeek()
		val weekIndex = CalendarUtil.getWeekIndex()
		return getTodayCourses(courses, currentWeek, weekIndex)
	}

	fun getTodayCourses(courses: Array<Course>, dayIndex: Int): ArrayList<Course> {
		val currentWeek = CalendarUtil.getWeek(dayIndex)
		val weekIndex = dayIndex % 7 + 1
		return getTodayCourses(courses, currentWeek, weekIndex)
	}

	private fun getTodayCourses(courses: Array<Course>, currentWeek: Int, weekIndex: Int): ArrayList<Course> {
		val list = ArrayList<Course>()
		courses.filter {
			try {
				val weekArray = it.week.split('-')
				val startWeek = weekArray[0].toInt()
				val endWeek = weekArray[1].toInt()
				var other = false
				when (it.type) {
					Constants.COURSE_TYPE_ERROR, Constants.COURSE_TYPE_ALL -> other = true
					Constants.COURSE_TYPE_SINGLE -> if (currentWeek % 2 == 1)
						other = true
					Constants.COURSE_TYPE_DOUBLE -> if (currentWeek % 2 == 0)
						other = true
					else -> other = false
				}
				currentWeek in startWeek..endWeek && other && (it.day.toInt()) == weekIndex
			} catch (e: Exception) {
				false
			}
		}
				.forEach {
					list.add(it)
				}
		return list
	}

	fun getTomorrowCourses(courses: Array<Course>): ArrayList<Course> {
		var weekIndex = CalendarUtil.getWeekIndex()//周数
		var dayIndex = CalendarUtil.getWeekIndex()//星期几
		dayIndex++
		if (dayIndex > 7) {
			weekIndex++
			dayIndex %= 7
		}
		return getTodayCourses(courses, weekIndex, dayIndex)
	}

	fun splitInfo(course: Course): Array<CourseTimeInfo> {
		val array = course.location.split('\n')
		return if (array.size > 1) {
			Array(array.size, { i ->
				val info = array[i]
				val location = info.substring(0, info.indexOfFirst { it == '(' })
				val week = info.substring(info.indexOfFirst { it == '(' } + 1, info.indexOfLast { it == ')' }) + '周'
				CourseTimeInfo(week, location)
			})
		} else
			arrayOf(CourseTimeInfo(course.week + '周', course.location))
	}

	fun mergeCourses(aList: ArrayList<ArrayList<ArrayList<Course>>>, bList: ArrayList<ArrayList<ArrayList<Course>>>): ArrayList<ArrayList<ArrayList<Course>>> {
		if (aList.isEmpty())
			return bList
		if (bList.isEmpty())
			return aList
		val list = ArrayList<ArrayList<ArrayList<Course>>>()
		for (i in 0 until aList.size)
			for (k in 0 until aList[i].size) {
				aList[i][k].addAll(bList[i][k])
			}
		return list
	}

	fun typeMerge(type1: String, type2: String): String {
		if (type1 == Constants.COURSE_TYPE_NOT)
			return type2
		if (type2 == Constants.COURSE_TYPE_NOT)
			return type1
		if (type1 == Constants.COURSE_TYPE_ERROR || type2 == Constants.COURSE_TYPE_ERROR)
			return Constants.COURSE_TYPE_ERROR
		return type1
	}

	private val needLoginStudents = ArrayList<Student>()
	private const val TAG = "CourseUtil_getCoursesFromServer"

	/**
	 * 从云端获取课表数据
	 */
	fun getCoursesFromServer(context: Context, year: String?, term: Int?, listener: GetCourseListener) {
		val studentList = XhuFileUtil.getArrayFromFile(XhuFileUtil.getStudentListFile(context), Student::class.java)
		val array = ArrayList<Observable<GetCourseRT>>()
		val updateList = ArrayList<Student>()
		val rtList = ArrayList<GetCourseRT>()
		if (Settings.isEnableMultiUserMode)
			studentList.forEach {
				array.add(getCourseFromServer(context, it, year, term))
				updateList.add(it)
			}
		else {
			var mainStudent: Student? = (0 until studentList.size)
					.firstOrNull { studentList[it].isMain }
					?.let { studentList[it] }
			if (mainStudent == null)
				mainStudent = studentList[0]
			array.add(getCourseFromServer(context, mainStudent, year, term))
			updateList.add(mainStudent)
		}
		Observable.merge(array)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<GetCourseRT> {
					override fun onComplete() {
						Logs.i(TAG, "onComplete: ")
						if (needLoginStudents.size != 0) {
							login(object : LoginListener {
								override fun loginDone() {
									getCoursesFromServer(context, year, term, listener)
								}

								override fun error(rt: Int, e: Throwable) {
									listener.error(rt, e)
								}
							})
							return
						}
						listener.got(updateList, rtList)
					}

					override fun onSubscribe(d: Disposable) {
						rtList.clear()
						listener.start()
					}

					override fun onNext(t: GetCourseRT) {
						rtList.add(t)
					}

					override fun onError(e: Throwable) {
						listener.error(-1, e)
					}
				})
	}

	private fun getCourseFromServer(context: Context, student: Student, year: String?, term: Int?): Observable<GetCourseRT> {
		return ScheduleHelper.tomcatRetrofit
				.create(StudentService::class.java)
				.getCourses(student.username, year, term)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map({ responseBody ->
					val getCourseRT = Gson().fromJson(InputStreamReader(responseBody.byteStream()), GetCourseRT::class.java)
					val parentFile = XhuFileUtil.getCourseCacheParentFile(context)
					if (!parentFile.exists())
						parentFile.mkdirs()
					val base64Name = XhuFileUtil.filterString(Base64.encodeToString(student.username.toByteArray(), Base64.DEFAULT))
					when (getCourseRT.rt) {
						ConstantsCode.DONE, ConstantsCode.SERVER_COURSE_ANALYZE_ERROR -> {//请求成功或者数据存在问题
							val newFile = File(parentFile, "$base64Name.temp")
							newFile.createNewFile()
							XhuFileUtil.saveObjectToFile(getCourseRT.courses, newFile)
							val newMD5 = XhuFileUtil.getMD5(newFile)
							val oldFile = File(parentFile, base64Name)
							var oldMD5 = ""
							if (oldFile.exists())
								oldMD5 = XhuFileUtil.getMD5(oldFile)!!
							if (newMD5 != oldMD5) {
								oldFile.delete()
								newFile.renameTo(oldFile)
							} else {
								newFile.delete()
							}
						}
						ConstantsCode.ERROR_NOT_LOGIN -> {//未登录
							needLoginStudents.add(student)
						}
					}
					getCourseRT
				})
	}

	/**
	 * 重新登陆更新cookie
	 */
	private fun login(listener: LoginListener) {
		Logs.i(TAG, "login: needLogin: ${needLoginStudents.size}")
		val array = ArrayList<Observable<AutoLoginRT>>()
		needLoginStudents.forEach {
			Logs.i(TAG, "login: add: ${it.username}")
			array.add(ScheduleHelper.tomcatRetrofit
					.create(UserService::class.java)
					.autoLogin(it.username, it.password)
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.map({ responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), AutoLoginRT::class.java) }))
		}
		Observable.merge(array)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : DisposableObserver<AutoLoginRT>() {
					override fun onComplete() {
						needLoginStudents.clear()
						listener.loginDone()
					}

					override fun onNext(autoLoginRT: AutoLoginRT) {
						Logs.i(TAG, "onNext: rt: ${autoLoginRT.rt}")
					}

					override fun onError(e: Throwable) {
						needLoginStudents.clear()
						listener.error(-1, e)
					}
				})
	}
}