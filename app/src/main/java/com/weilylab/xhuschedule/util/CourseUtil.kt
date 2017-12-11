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

    fun formatCourses(courses: Array<Course>): Array<Array<LinkedList<Course>>> {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        val currentWeek = CalendarUtil.getWeek()
        ScheduleHelper.weekIndex = currentWeek
        return formatCourses(courses, currentWeek)
    }

    fun formatCourses(courses: Array<Course>, weekIndex: Int): Array<Array<LinkedList<Course>>> {
        val array = Array(11, { Array<LinkedList<Course>>(7, { LinkedList() }) })
        courses.forEach {
            try {
                var other = false
                when (it.type) {
                    "-1", "0" -> other = true
                    "1" -> if (weekIndex % 2 == 1)
                        other = true
                    "2" -> if (weekIndex % 2 == 0)
                        other = true
                    else -> other = false
                }
                val weekArray = it.week.split('-')
                val startWeek = weekArray[0].toInt()
                val endWeek = weekArray[1].toInt()
                if ((weekIndex !in startWeek..endWeek) || !other)
                    it.type = "not"
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
        return array
    }

    fun getWeekCourses(courses: Array<Course>): Array<Array<LinkedList<Course>>> {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        val currentWeek = CalendarUtil.getWeek()
        ScheduleHelper.weekIndex = currentWeek
        return getWeekCourses(courses, currentWeek)
    }

    fun getWeekCourses(courses: Array<Course>, weekIndex: Int): Array<Array<LinkedList<Course>>> {
        ScheduleHelper.weekIndex = weekIndex
        val array = Array(11, { Array<LinkedList<Course>>(7, { LinkedList() }) })
        courses.filter {
            try {
                var other = false
                when (it.type) {
                    "-1", "0" -> other = true
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
        return array
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
                    "-1", "0" -> other = true
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

    fun mergeCourses(aList: Array<Array<LinkedList<Course>>>, bList: Array<Array<LinkedList<Course>>>): Array<Array<LinkedList<Course>>> {
        if (aList.isEmpty())
            return bList
        if (bList.isEmpty())
            return aList
        return Array(11, { time ->
            Array(7, { day ->
                val linkedList = LinkedList<Course>()
                linkedList.addAll(aList[time][day])
                linkedList.addAll(bList[time][day])
                linkedList
            })
        })
    }

    fun typeMerge(type1: String, type2: String): String {
        if (type1 == "not")
            return type2
        if (type2 == "not")
            return type1
        if (type1 == "-1" || type2 == "-1")
            return "-1"
        return type1
    }
}