/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jinrishici.sdk.android.model.PoetySentence
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.StudentInfo
import com.weilylab.xhuschedule.repository.*
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.*
import vip.mystery0.tools.ResourceException
import vip.mystery0.tools.utils.toDateString
import java.util.*

class BottomNavigationViewModel : ViewModel(), KoinComponent {
	private val bottomNavigationRepository: BottomNavigationRepository by inject()
	private val studentRepository: StudentRepository by inject()
	private val customThingRepository: CustomThingRepository by inject()
	private val noticeRepository: NoticeRepository by inject()
	private val feedBackRepository: FeedBackRepository by inject()
	private val courseRepository: CourseRepository by inject()
	private val initRepository: InitRepository by inject()

	//学生列表
	val studentList by lazy { MutableLiveData<PackageData<List<Student>>>() }

	//主用户信息
	val studentInfo by lazy { MutableLiveData<StudentInfo>() }

	//今日诗词
	val poetySentence by lazy { MutableLiveData<PoetySentence>() }

	//全部课程列表
	val courseList by lazy { MutableLiveData<PackageData<List<Schedule>>>() }

	//自定义事项列表
	val customThingList by lazy { MutableLiveData<List<CustomThing>>() }

	//今日课程列表
	val todayCourseList by lazy { MutableLiveData<List<Schedule>>() }

	//滑动展示的课程列表，同一个位置多节课程的情况
	val showCourse by lazy { MutableLiveData<List<Schedule>>() }

	//当前周数
	val currentWeek by lazy { MutableLiveData<PackageData<Int>>() }

	//周课表展示的周数
	val week by lazy { MutableLiveData<Int>() }

	//开学时间
	val startDateTime by lazy { MutableLiveData<Calendar>() }

	//是否有新的公告
	val newNotice by lazy { MutableLiveData<PackageData<Boolean>>() }

	//是否有新的消息
	val newFeedback by lazy { MutableLiveData<PackageData<Boolean>>() }

	//顶部标题
	val title by lazy { MutableLiveData<Pair<Class<*>, String>>() }

	//Toast信息
	val toastMessage by lazy { MutableLiveData<Int>() }

	fun init() {
		launch(studentList) {
			studentList.loading()
			val list = studentRepository.queryAllStudentList()
			if (list.isEmpty()) {
				studentList.empty()
				return@launch
			}
			studentList.content(list)
			val nowString = Calendar.getInstance().toDateString()
			//查询主用户信息
			val mainStudent = list.find { it.isMain }
					?: throw ResourceException(R.string.hint_null_student)
			val info = studentRepository.queryStudentInfo(mainStudent, nowString == ConfigurationUtil.lastUpdateDate)
			studentInfo.postValue(info)

			val enableMultiUserMode = withContext(Dispatchers.IO) { ConfigurationUtil.isEnableMultiUserMode }
			//查询课程列表
			courseList.loading()
			if (enableMultiUserMode) {
				val courses = bottomNavigationRepository.queryCoursesForManyStudent(list, fromCache = true, throwError = false)
				if (courses.isNullOrEmpty()) {
					courseList.empty()
				} else {
					courseList.content(courses)
				}
				//处理今日课程
				val todayCourse = courseRepository.getTodayCourse(courses)
				todayCourseList.postValue(todayCourse)
			} else {
				val courses = bottomNavigationRepository.queryCourses(mainStudent, fromCache = true, throwError = false)
				if (courses.isNullOrEmpty()) {
					courseList.empty()
				} else {
					courseList.content(courses)
				}
				//处理今日课程
				val todayCourse = courseRepository.getTodayCourse(courses)
				todayCourseList.postValue(todayCourse)
			}
			//查询自定义事项
			val today = customThingRepository.getToday()
			customThingList.postValue(today)
			//如果是当天第一次启动，那么刷新在线数据
			if (nowString != ConfigurationUtil.lastUpdateDate) {
				queryOnline(throwError = false)
			}
		}
	}

	fun queryOnline() {
		launch(courseList) {
			queryOnline(true)
		}
	}

	private suspend fun queryOnline(throwError: Boolean) {
		courseList.loading()
		val list = studentRepository.queryAllStudentList()
		val enableMultiUserMode = withContext(Dispatchers.IO) { ConfigurationUtil.isEnableMultiUserMode }
		if (enableMultiUserMode) {
			val courses = bottomNavigationRepository.queryCoursesForManyStudent(list, fromCache = false, throwError = throwError)
			courseList.content(courses)
			//处理今日课程
			val todayCourse = courseRepository.getTodayCourse(courses)
			todayCourseList.postValue(todayCourse)
		} else {
			//查询主用户信息
			val mainStudent = list.find { it.isMain }
					?: throw ResourceException(R.string.hint_null_student)
			val courses = bottomNavigationRepository.queryCourses(mainStudent, fromCache = false, throwError = throwError)
			courseList.content(courses)
		}
		toastMessage.postValue(R.string.hint_course_sync_done)
		ConfigurationUtil.lastUpdateDate = Calendar.getInstance().toDateString()
	}

	fun queryCurrentWeek() {
		launch(currentWeek) {
			startDateTime.value?.let {
				withContext(Dispatchers.Default) {
					val weekIndex = CalendarUtil.getWeekFromCalendar(it)
					week.postValue(weekIndex)
					currentWeek.content(weekIndex)
				}
				return@launch
			}
			val startTime = initRepository.getStartTime()
			startDateTime.postValue(startTime)
			val weekIndex = CalendarUtil.getWeekFromCalendar(startTime)
			week.postValue(weekIndex)
			currentWeek.content(weekIndex)
		}
	}

	fun queryNewNotice() {
		launch(newNotice) {
			withContext(Dispatchers.Default) {
				newNotice.content(noticeRepository.queryNotice(true))
			}
		}
	}

	fun queryNewFeedbackMessage() {
		launch(newFeedback) {
			withContext(Dispatchers.Default) {
				val list = studentRepository.queryAllStudentList()
				//查询主用户信息
				val mainStudent = list.find { it.isMain }
						?: throw ResourceException(R.string.hint_null_student)
				newFeedback.content(feedBackRepository.queryNewFeedback(mainStudent))
			}
		}
	}
}