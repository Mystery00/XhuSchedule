package com.weilylab.xhuschedule.repository

import android.graphics.Color
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.local.TestLocalDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.userDo.CourseUtil
import com.weilylab.xhuschedule.utils.userDo.TestUtil
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.StringTools
import java.util.*
import kotlin.collections.ArrayList

object WidgetRepository {
	/**
	 * 查询学生的今日课程信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	fun queryTodayCourse(): List<Schedule> {
		val studentList = StudentLocalDataSource.queryAllStudentListDo()
		val mainStudent = UserUtil.findMainStudent(studentList) ?: return emptyList()
		val week = CalendarUtil.getWeekFromCalendar(InitLocalDataSource.getStartDateTime())
		val weekIndex = CalendarUtil.getWeekIndex()
		return CourseLocalDataSource.getRowCourseList(mainStudent)
				.filter { CourseUtil.isTodayCourse(it, week, weekIndex) }
	}

	/**
	 * 查询学生的考试信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	fun queryTests(): List<Test> {
		val studentList = StudentLocalDataSource.queryAllStudentListDo()
		val mainStudent = UserUtil.findMainStudent(studentList) ?: return emptyList()
		return sortTests(TestUtil.filterTestList(TestLocalDataSource.getRawTestList(mainStudent)))
	}

	/**
	 * 查询学生的考试信息
	 * 查询多个用户的信息
	 * 同步方法
	 */
	fun queryTestsForManyStudent(): List<Test> {
		val studentList = StudentLocalDataSource.queryAllStudentListDo()
		val tests = ArrayList<Test>()
		studentList.forEach {
			tests.addAll(TestUtil.filterTestList(TestLocalDataSource.getRawTestList(it)))
		}
		return sortTests(tests)
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

	/**
	 * 根据考试时间进行排序
	 */
	private fun sortTests(list: List<Test>): List<Test> {
		val now = Calendar.getInstance().timeInMillis
		return list.sortedBy { it.formatTestDate(now) }
	}

	/**
	 * 计算排序的参数值
	 */
	private fun Test.formatTestDate(now: Long): Long {
		return try {
			val dayArray = this.date.split('-')
			val startTimeArray = this.time.split('-')[0].split(':')
			val endTimeArray = this.time.split('-')[1].split(':')
			val startCalendar = Calendar.getInstance()
			startCalendar.set(dayArray[0].toInt(), dayArray[1].toInt() - 1, dayArray[2].toInt(), startTimeArray[0].toInt(), startTimeArray[1].toInt(), 0)
			val endCalendar = Calendar.getInstance()
			endCalendar.set(dayArray[0].toInt(), dayArray[1].toInt() - 1, dayArray[2].toInt(), endTimeArray[0].toInt(), endTimeArray[1].toInt(), 0)
			val startTime = startCalendar.timeInMillis
			val endTime = endCalendar.timeInMillis
			return if (now > endTime)//考试已结束
				startTime + 365 * 24 * 60 * 60 * 1000L
			else
				startTime
		} catch (e: Exception) {
			Logs.wtf("formatTestDate: ", e)
			now
		}
	}
}