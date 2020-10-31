/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.repository

import android.graphics.Color
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.zhuangfei.timetable.model.Schedule
import org.koin.core.KoinComponent
import org.koin.core.inject
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.md5
import java.util.*
import kotlin.collections.ArrayList

class WidgetRepository : KoinComponent {
    private val studentRepository: StudentRepository by inject()
    private val courseRepository: CourseRepository by inject()
    private val customThingRepository: CustomThingRepository by inject()
    private val testRepository: TestRepository by inject()
    private val initRepository: InitRepository by inject()

    /**
     * 查询学生的今日课程信息
     * 查询主用户的信息
     * 同步方法
     */
    suspend fun queryTodayCourse(): List<Schedule> {
        val mainStudent = studentRepository.queryMainStudent() ?: return emptyList()
        val week = CalendarUtil.getWeekFromCalendar(initRepository.getStartDateTime())
        val weekIndex = CalendarUtil.getWeekIndex()
        return courseRepository.queryCourseByUsernameAndTerm(mainStudent, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm, fromCache = true, throwError = true)
                .map { it.schedule }
                .filter { it.day == weekIndex && it.weekList.contains(week) }
    }

    /**
     * 查询学生的考试信息
     * 查询主用户的信息
     * 同步方法
     */
    suspend fun queryTests(): List<Test> {
        val mainStudent = studentRepository.queryMainStudent() ?: return emptyList()
        return sortTests(testRepository.queryAll(mainStudent)
                .filter { it.date != "" || it.testno != "" || it.time != "" || it.location != "" })
    }

    /**
     * 查询学生的考试信息
     * 查询多个用户的信息
     * 同步方法
     */
    suspend fun queryTestsForManyStudent(): List<Test> {
        val studentList = studentRepository.queryAllStudentList()
        val tests = ArrayList<Test>()
        studentList.forEach { student ->
            tests.addAll(testRepository.queryAll(student)
                    .filter { it.date != "" || it.testno != "" || it.time != "" || it.location != "" })
        }
        return sortTests(tests)
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