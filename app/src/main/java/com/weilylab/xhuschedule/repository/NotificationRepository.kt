package com.weilylab.xhuschedule.repository

import android.graphics.Color
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.repository.local.CourseLocalDataSource
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.repository.local.TestLocalDataSource
import com.weilylab.xhuschedule.utils.userDo.TestUtil
import com.weilylab.xhuschedule.utils.userDo.UserUtil
import com.zhuangfei.timetable.model.Schedule
import vip.mystery0.tools.utils.StringTools
import kotlin.collections.ArrayList

object NotificationRepository {
	/**
	 * 查询学生的今日课程信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	fun queryTomorrowCourse(studentList: List<Student>): List<Schedule> {
		val mainStudent = UserUtil.findMainStudent(studentList) ?: return emptyList()
		return CourseLocalDataSource.getRowCourseList(mainStudent)
	}

	/**
	 * 查询学生的今日课程信息
	 * 查询多个用户的信息
	 * 同步方法
	 */
	fun queryTomorrowCourseForManyStudent(studentList: List<Student>): List<Schedule> {
		val list = ArrayList<Schedule>()
		studentList.forEach {
			list.addAll(CourseLocalDataSource.getRowCourseList(it))
		}
		return list
	}

	/**
	 * 查询学生的考试信息
	 * 查询主用户的信息
	 * 同步方法
	 */
	fun queryTests(studentList: List<Student>): List<Test> {
		val mainStudent = UserUtil.findMainStudent(studentList) ?: return emptyList()
		return TestUtil.filterTestList(TestLocalDataSource.getRawTestList(mainStudent))
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
		return tests
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
				val md5Int = StringTools.md5(test.name).substring(0, 1).toInt(16)
				ColorPoolHelper.colorPool.getColorAuto(md5Int)
			}
			colorList.add(color)
		}
		return colorList.toIntArray()
	}
}