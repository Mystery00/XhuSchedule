package com.weilylab.xhuschedule.repository

import android.graphics.Color
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.model.Course
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.model.Schedule
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.tools.utils.md5
import java.util.*
import kotlin.collections.ArrayList

class NotificationRepository : KoinComponent {
	private val courseRepository: CourseRepository by inject()
	private val customThingRepository: CustomThingRepository by inject()
	private val testRepository: TestRepository by inject()
	private val initRepository: InitRepository by inject()

	/**
	 * 查询学生的明日课程信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	suspend fun queryTomorrowCourse(studentList: List<Student>): List<Schedule> {
		val mainStudent = studentList.find { it.isMain } ?: return emptyList()
		val startTime = initRepository.getStartDateTime()
		val tomorrowWeek = CalendarUtil.getTomorrowWeekFromCalendar(startTime)
		val tomorrow = CalendarUtil.getTomorrowIndex()
		val year = ConfigurationUtil.currentYear
		val term = ConfigurationUtil.currentTerm
		val courseList = ArrayList<Course>()
		courseList.addAll(courseRepository.queryCourseByUsernameAndTerm(mainStudent, year, term, fromCache = true, throwError = true))
		courseList.addAll(courseRepository.queryCustomCourseByTerm(mainStudent, year, term))
		return courseList.map { it.schedule }.filter { it.weekList.contains(tomorrowWeek) && it.day == tomorrow }
	}

	/**
	 * 查询学生的明日课程信息
	 * 查询多个用户的信息
	 * 同步方法
	 */
	suspend fun queryTomorrowCourseForManyStudent(studentList: List<Student>): List<Schedule> {
		val startTime = initRepository.getStartDateTime()
		val tomorrowWeek = CalendarUtil.getTomorrowWeekFromCalendar(startTime)
		val tomorrow = CalendarUtil.getTomorrowIndex()
		val list = ArrayList<Course>()
		val year = ConfigurationUtil.currentYear
		val term = ConfigurationUtil.currentTerm
		studentList.forEach {
			list.addAll(courseRepository.queryCourseByUsernameAndTerm(it, year, term, fromCache = true, throwError = true))
			list.addAll(courseRepository.queryCustomCourseByTerm(it, year, term))
		}
		return list.map { it.schedule }.filter { it.weekList.contains(tomorrowWeek) && it.day == tomorrow }
	}

	/**
	 * 查询明日自定义事项信息
	 * 同步方法
	 */
	suspend fun queryTomorrowCustomThing(): List<CustomThing> {
		val tomorrow = Calendar.getInstance()
		tomorrow.add(Calendar.DAY_OF_YEAR, 1)
		return customThingRepository.getAll().filter { CalendarUtil.isThingOnDay(it, tomorrow) }
	}

	/**
	 * 查询学生的考试信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	suspend fun queryTests(studentList: List<Student>): List<Test> {
		val mainStudent = studentList.find { it.isMain } ?: return emptyList()
		return testRepository.queryAll(mainStudent)
				.filter { (it.date != "" || it.testno != "" || it.time != "" || it.location != "") && CalendarUtil.isTomorrowTest(it.date) }
	}

	/**
	 * 查询学生的考试信息
	 * 查询多个用户的信息
	 * 同步方法
	 */
	suspend fun queryTestsForManyStudent(studentList: List<Student>): List<Test> {
		val tests = ArrayList<Test>()
		studentList.forEach {
			tests.addAll(testRepository.queryAll(it))
		}
		return tests.filter { CalendarUtil.isTomorrowTest(it.date) }
	}

	/**
	 * 根据考试列表生成对应的文字颜色
	 */
	suspend fun generateColorList(list: List<Test>): IntArray {
		val colorList = ArrayList<Int>()
		val courseList = courseRepository.queryDistinctCourseByUsernameAndTerm()
		list.forEach { test ->
			val course = courseList.find { it.name == test.name }
			val color = try {
				Color.parseColor(course!!.color)
			} catch (e: Exception) {
				val md5Int = test.name.md5().substring(0, 1).toInt(16)
				ColorPoolHelper.colorPool.getColorAuto(md5Int)
			}
			colorList.add(color)
		}
		return colorList.toIntArray()
	}
}