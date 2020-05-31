package com.weilylab.xhuschedule.repository

import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.model.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.utils.isConnectInternet
import java.util.*
import kotlin.collections.ArrayList

class BottomNavigationRepository : KoinComponent {
	private val courseRepository: CourseRepository by inject()

	private val xhuScheduleCloudAPI: XhuScheduleCloudAPI by inject()

	/**
	 * 获取缓存的所有课程的列表
	 * 多用户模式
	 */
	suspend fun queryCoursesForManyStudent(studentList: List<Student>,
										   fromCache: Boolean,
										   throwError: Boolean): List<Schedule> {
		val year = withContext(Dispatchers.IO) { ConfigurationUtil.currentYear }
		val term = withContext(Dispatchers.IO) { ConfigurationUtil.currentTerm }
		val result = ArrayList<Schedule>()
		studentList.forEach { student ->
			val courseList = courseRepository.queryCourseByUsernameAndTerm(student, year, term, fromCache, throwError)
			val customCourseList = courseRepository.queryCustomCourseByTerm(student, year, term)
			val all = ArrayList<Course>()
			all.addAll(courseList)
			all.addAll(customCourseList)
			result.addAll(all.map { it.schedule })
		}
		return result
	}

	/**
	 * 获取缓存的所有课程列表
	 */
	suspend fun queryCourses(mainStudent: Student,
							 fromCache: Boolean,
							 throwError: Boolean): List<Schedule> {
		val year = withContext(Dispatchers.IO) { ConfigurationUtil.currentYear }
		val term = withContext(Dispatchers.IO) { ConfigurationUtil.currentTerm }
		val courseList = courseRepository.queryCourseByUsernameAndTerm(mainStudent, year, term, fromCache, throwError)
		val customCourseList = courseRepository.queryCustomCourseByTerm(mainStudent, year, term)
		val all = ArrayList<Course>()
		all.addAll(courseList)
		all.addAll(customCourseList)
		return all.map { it.schedule }
	}

	private suspend fun getLocalStartDateTime(): Calendar = withContext(Dispatchers.Default) {
		val calendar = Calendar.getInstance()
		val dateString = if (ConfigurationUtil.isCustomStartTime)
			ConfigurationUtil.customStartTime
		else
			ConfigurationUtil.startTime
		if (dateString == "")
			return@withContext calendar
		val dateArray = dateString.split('-')
		calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt(), 0, 0, 0)
		return@withContext calendar
	}

	suspend fun getOnlineStartDateTime(): Calendar {
		if (isConnectInternet()) {
			withContext(Dispatchers.IO) {
				val startDateTimeResponse = xhuScheduleCloudAPI.requestStartDateTime()
				if (startDateTimeResponse.isSuccessful) {
					ConfigurationUtil.startTime = startDateTimeResponse.data
				}
			}
		}
		return getLocalStartDateTime()
	}
}