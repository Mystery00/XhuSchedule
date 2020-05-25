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
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.zhuangfei.timetable.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.rx.*
import vip.mystery0.tools.ResourceException
import java.util.*

class BottomNavigationViewModel : ViewModel(), KoinComponent {
	private val bottomNavigationRepository: BottomNavigationRepository by inject()
	private val loginRepository: LoginRepository by inject()
	private val customThingRepository: CustomThingRepository by inject()
	private val noticeRepository: NoticeRepository by inject()
	private val feedBackRepository: FeedBackRepository by inject()

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

	fun init() {
		launch(studentList) {
			studentList.loading()
			val list = bottomNavigationRepository.queryAllStudent()
			if (list.isEmpty()) {
				studentList.empty()
				return@launch
			}
			studentList.content(list)
			//查询主用户信息
			val mainStudent = UserUtil.findMainStudent(list)
					?: throw ResourceException(R.string.hint_null_student)
			val info = loginRepository.queryStudentInfo(mainStudent)
			studentInfo.postValue(info)

			val enableMultiUserMode = withContext(Dispatchers.IO) { ConfigurationUtil.isEnableMultiUserMode }
			//查询课程列表
			courseList.loading()
			if (enableMultiUserMode) {
				val courses = bottomNavigationRepository.queryCoursesForManyStudent(list, fromCache = true, throwError = false)
				courseList.content(courses)
				//处理今日课程
				val todayCourse = CourseUtil.getTodayCourse(courses)
				todayCourseList.postValue(todayCourse)
			} else {
				val courses = bottomNavigationRepository.queryCourses(mainStudent, fromCache = true, throwError = false)
				courseList.content(courses)
			}
			//查询自定义事项
			val today = customThingRepository.getToday()
			customThingList.postValue(today)
			//如果是当天第一次启动，那么刷新在线数据
			val nowString = CalendarUtil.getTodayDateString()
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
		val list = bottomNavigationRepository.queryAllStudent()
		val enableMultiUserMode = withContext(Dispatchers.IO) { ConfigurationUtil.isEnableMultiUserMode }
		if (enableMultiUserMode) {
			val courses = bottomNavigationRepository.queryCoursesForManyStudent(list, fromCache = false, throwError = throwError)
			courseList.content(courses)
			//处理今日课程
			val todayCourse = CourseUtil.getTodayCourse(courses)
			todayCourseList.postValue(todayCourse)
		} else {
			//查询主用户信息
			val mainStudent = UserUtil.findMainStudent(list)
					?: throw ResourceException(R.string.hint_null_student)
			val courses = bottomNavigationRepository.queryCourses(mainStudent, fromCache = false, throwError = throwError)
			courseList.content(courses)
		}
	}

	fun queryCurrentWeek() {
		launch(currentWeek) {
			startDateTime.value?.let {
				withContext(Dispatchers.Default) {
					val weekIndex = CalendarUtil.getWeekFromCalendar(CalendarUtil.startDateTime)
					week.postValue(weekIndex)
					currentWeek.content(weekIndex)
				}
				return@launch
			}
			val startTime = bottomNavigationRepository.getOnlineStartDateTime()
			CalendarUtil.startDateTime = startTime
			val weekIndex = CalendarUtil.getWeekFromCalendar(startTime)
			week.postValue(weekIndex)
			currentWeek.content(weekIndex)
		}
	}

	fun queryNewNotice() {
		launch(newNotice) {
			newNotice.content(noticeRepository.queryNotice(true))
		}
	}

	fun queryNewFeedbackMessage() {
		launch(newFeedback) {
			val list = bottomNavigationRepository.queryAllStudent()
			//查询主用户信息
			val mainStudent = UserUtil.findMainStudent(list)
					?: throw ResourceException(R.string.hint_null_student)
			newFeedback.content(feedBackRepository.queryNewFeedback(mainStudent))
		}
	}
}