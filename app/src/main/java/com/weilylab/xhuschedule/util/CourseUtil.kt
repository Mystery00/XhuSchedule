/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-26 下午8:22
 */

package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.CourseTimeInfo
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by myste.
 */
object CourseUtil {

    fun formatCourses(courses: Array<Course>): ArrayList<LinkedList<Course>> {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        val currentWeek = CalendarUtil.getWeek()
        ScheduleHelper.weekIndex = currentWeek
        return formatCourses(courses, currentWeek)
    }

    fun formatCourses(courses: Array<Course>, weekIndex: Int): ArrayList<LinkedList<Course>> {
        val tempArray = Array(5, { Array<LinkedList<Course>>(7, { LinkedList() }) })
        courses.forEach {
            try {
                var other = false
                when (it.type) {
                    "0" -> other = true
                    "1" -> if (weekIndex % 2 == 1)
                        other = true
                    "2" -> if (weekIndex % 2 == 0)
                        other = true
                    else -> other = false
                }
                val weekArray = it.week.split('-')
                val startWeek = weekArray[0].toInt()
                val endWeek = weekArray[1].toInt()
                if (weekIndex !in startWeek..endWeek || !other)
                    it.type = "not"
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val timeArray = it.time.split('-')
            val startTime = (timeArray[0].toInt() - 1) / 2
            val endTime = (timeArray[1].toInt()) / 2
            for (index in startTime until endTime) {
                var flag = false
                for (temp in tempArray[index][it.day.toInt() - 1]) {
                    flag = temp.with(it)
                    if (flag)
                        break
                }
                if (!flag)
                    tempArray[index][it.day.toInt() - 1].add(it)
            }
        }
        val list = ArrayList<LinkedList<Course>>()
        tempArray.forEach {
            it.forEach {
                list.add(it)
            }
        }
        return list
    }

    fun getCourses(courses: Array<Course>): ArrayList<LinkedList<Course>> {
        val tempArray = Array(5, { Array<LinkedList<Course>>(7, { LinkedList() }) })
        courses.forEach {
            val timeArray = it.time.split('-')
            val startTime = (timeArray[0].toInt() - 1) / 2
            val endTime = (timeArray[1].toInt()) / 2
            for (index in startTime until endTime) {
                var flag = false
                for (temp in tempArray[index][it.day.toInt() - 1]) {
                    flag = temp.with(it)
                    if (flag)
                        break
                }
                if (!flag)
                    tempArray[index][it.day.toInt() - 1].add(it)
            }
        }
        val list = ArrayList<LinkedList<Course>>()
        tempArray.forEach {
            it.forEach {
                list.add(it)
            }
        }
        return list
    }

    fun getWeekCourses(courses: Array<Course>): ArrayList<LinkedList<Course>> {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        val currentWeek = CalendarUtil.getWeek()
        ScheduleHelper.weekIndex = currentWeek
        return getWeekCourses(courses, currentWeek)
    }

    fun getWeekCourses(courses: Array<Course>, weekIndex: Int): ArrayList<LinkedList<Course>> {
        ScheduleHelper.weekIndex = weekIndex
        val tempArray = Array(5, { Array<LinkedList<Course>>(7, { LinkedList() }) })
        courses.filter {
            try {
                var other = false
                when (it.type) {
                    "0" -> other = true
                    "1" -> if (weekIndex % 2 == 1)
                        other = true
                    "2" -> if (weekIndex % 2 == 0)
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
            val startTime = (timeArray[0].toInt() - 1) / 2
            val endTime = (timeArray[1].toInt()) / 2
            for (index in startTime until endTime) {
                var flag = false
                for (temp in tempArray[index][it.day.toInt() - 1]) {
                    flag = temp.with(it)
                    if (flag)
                        break
                }
                if (!flag)
                    tempArray[index][it.day.toInt() - 1].add(it)
            }
        }
        val list = ArrayList<LinkedList<Course>>()
        tempArray.forEach {
            it.forEach {
                list.add(it)
            }
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
        val list = ArrayList<Course>()
        courses.filter {
            try {
                val weekArray = it.week.split('-')
                val startWeek = weekArray[0].toInt()
                val endWeek = weekArray[1].toInt()
                var other = false
                when (it.type) {
                    "0" -> other = true
                    "1" -> if (currentWeek % 2 == 1)
                        other = true
                    "2" -> if (currentWeek % 2 == 0)
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

    fun mergeCourses(aList: LinkedList<LinkedList<Course>>, bList: LinkedList<LinkedList<Course>>): LinkedList<LinkedList<Course>> {
        if (aList.size == 0)
            return bList
        if (bList.size == 0)
            return aList
        val list = LinkedList<LinkedList<Course>>()
        for (i in 0 until aList.size) {
            list.add(LinkedList())
            list[i].addAll(aList[i])
            list[i].addAll(bList[i])
        }
        return list
    }
}