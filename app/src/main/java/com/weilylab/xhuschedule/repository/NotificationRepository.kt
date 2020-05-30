package com.weilylab.xhuschedule.repository

import android.graphics.Color
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.userDo.TestUtil
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.utils.StringTools
import java.util.*
import kotlin.collections.ArrayList

object NotificationRepository {
	/**
	 * 查询学生的明日课程信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	fun queryTomorrowCourse(studentList: List<Student>): List<Schedule> {
		val mainStudent = UserUtil.findMainStudent(studentList) ?: return emptyList()
		val startTime = InitLocalDataSource.getStartDateTime()
		val tomorrowWeek = CalendarUtil.getTomorrowWeekFromCalendar(startTime)
		val tomorrow = CalendarUtil.getTomorrowIndex()
		val courseList = CourseLocalDataSource.getRowCourseList(mainStudent)
		return courseList.filter { it.weekList.contains(tomorrowWeek) && it.day == tomorrow }
	}

	/**
	 * 查询学生的明日课程信息
	 * 查询多个用户的信息
	 * 同步方法
	 */
	fun queryTomorrowCourseForManyStudent(studentList: List<Student>): List<Schedule> {
		val startTime = InitLocalDataSource.getStartDateTime()
		val tomorrowWeek = CalendarUtil.getTomorrowWeekFromCalendar(startTime)
		val tomorrow = CalendarUtil.getTomorrowIndex()
		val list = ArrayList<Schedule>()
		studentList.forEach {
			list.addAll(CourseLocalDataSource.getRowCourseList(it))
		}
		return list.filter { it.weekList.contains(tomorrowWeek) && it.day == tomorrow }
	}

	/**
	 * 查询明日自定义事项信息
	 * 同步方法
	 */
	fun queryTomorrowCustomThing(): List<CustomThing> {
		val tomorrow = Calendar.getInstance()
		tomorrow.add(Calendar.DAY_OF_YEAR, 1)
		return CustomThingLocalDataSource.getRawCustomThingList().filter { CalendarUtil.isThingOnDay(it, tomorrow) }
	}

	/**
	 * 查询学生的考试信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	fun queryTests(studentList: List<Student>): List<Test> {
		val mainStudent = UserUtil.findMainStudent(studentList) ?: return emptyList()
		return TestUtil.filterTestList(TestLocalDataSource.getRawTestList(mainStudent))
				.filter { CalendarUtil.isTomorrowTest(it.date) }
	}

	/**
	 * 查询学生的考试信息
	 * 查询多个用户的信息
	 * 同步方法
	 */
	fun queryTestsForManyStudent(studentList: List<Student>): List<Test> {
		val tests = ArrayList<Test>()
		studentList.forEach {
			tests.addAll(TestUtil.filterTestList(TestLocalDataSource.getRawTestList(it)))
		}
		return tests.filter { CalendarUtil.isTomorrowTest(it.date) }
	}

	/**
	 * 根据考试列表生成对应的文字颜色
	 */
	fun generateColorList(list: List<Test>): IntArray {
		val colorList = ArrayList<Int>()
		val courseList = CourseLocalDataSource.getDistinctRowCourseList()
		list.forEach { test ->
			val course = courseList.find { it.name == test.name }
			val color = try {
				Color.parseColor(course!!.color)
			} catch (e: Exception) {
				val md5Int = StringTools.instance.md5(test.name).substring(0, 1).toInt(16)
				ColorPoolHelper.colorPool.getColorAuto(md5Int)
			}
			colorList.add(color)
		}
		return colorList.toIntArray()
	}
}