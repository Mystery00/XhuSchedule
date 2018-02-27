/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.CourseTimeInfo
import kotlin.collections.ArrayList

/**
 * Created by myste.
 */
object CourseUtil {
    fun getAllCourses(courses: Array<Course>): ArrayList<ArrayList<ArrayList<Course>>> {
        val array = Array(11, { Array<ArrayList<Course>>(7, { ArrayList() }) })
        courses.forEach {
            try {//尝试解析时间
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val list = ArrayList<ArrayList<ArrayList<Course>>>()
        for (i in 0 until array.size) {
            list.add(ArrayList())
            for (k in 0 until array[i].size)
                list[i].add(array[i][k])
        }
        return list
    }

    fun formatCourses(courses: Array<Course>): ArrayList<ArrayList<ArrayList<Course>>> {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        val currentWeek = CalendarUtil.getWeek()
        ScheduleHelper.weekIndex = currentWeek
        return formatCourses(courses, currentWeek)
    }

    fun formatCourses(courses: Array<Course>, weekIndex: Int): ArrayList<ArrayList<ArrayList<Course>>> {
        val array = Array(11, { Array<ArrayList<Course>>(7, { ArrayList() }) })
        courses.forEach {
            try {//尝试解析周数
                var other = false
                when (it.type) {
                    Constants.COURSE_TYPE_ERROR, Constants.COURSE_TYPE_ALL -> other = true
                    Constants.COURSE_TYPE_SINGLE -> if (weekIndex % 2 == 1)
                        other = true
                    Constants.COURSE_TYPE_DOUBLE -> if (weekIndex % 2 == 0)
                        other = true
                    else -> other = false
                }
                val weekArray = it.week.split('-')
                val startWeek = weekArray[0].toInt()
                val endWeek = weekArray[1].toInt()
                if ((weekIndex !in startWeek..endWeek) || !other)
                    it.type = Constants.COURSE_TYPE_NOT
            } catch (e: Exception) {
                e.printStackTrace()
                ScheduleHelper.isAnalysisError = true
            }
            try {//尝试解析时间
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
            } catch (e: Exception) {
                e.printStackTrace()
                ScheduleHelper.isAnalysisError = true
            }
        }
        val list = ArrayList<ArrayList<ArrayList<Course>>>()
        for (i in 0 until array.size) {
            list.add(ArrayList())
            for (k in 0 until array[i].size)
                list[i].add(array[i][k])
        }
        return list
    }

    fun getWeekCourses(courses: Array<Course>): ArrayList<ArrayList<ArrayList<Course>>> {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        val currentWeek = CalendarUtil.getWeek()
        ScheduleHelper.weekIndex = currentWeek
        return getWeekCourses(courses, currentWeek)
    }

    fun getWeekCourses(courses: Array<Course>, weekIndex: Int): ArrayList<ArrayList<ArrayList<Course>>> {
        ScheduleHelper.weekIndex = weekIndex
        val array = Array(11, { Array<ArrayList<Course>>(7, { ArrayList() }) })
        courses.filter {
            try {
                var other = false
                when (it.type) {
                    Constants.COURSE_TYPE_ERROR, Constants.COURSE_TYPE_ALL -> other = true
                    Constants.COURSE_TYPE_SINGLE -> if (weekIndex % 2 == 1)
                        other = true
                    Constants.COURSE_TYPE_DOUBLE -> if (weekIndex % 2 == 0)
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
        val list = ArrayList<ArrayList<ArrayList<Course>>>()
        for (i in 0 until array.size) {
            list.add(ArrayList())
            for (k in 0 until array[i].size)
                list[i].add(array[i][k])
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
        return getTodayCourses(courses, currentWeek, weekIndex)
    }

    fun getTodayCourses(courses: Array<Course>, dayIndex: Int): ArrayList<Course> {
        val currentWeek = CalendarUtil.getWeek(dayIndex)
        val weekIndex = dayIndex % 7 + 1
        return getTodayCourses(courses, currentWeek, weekIndex)
    }

    private fun getTodayCourses(courses: Array<Course>, currentWeek: Int, weekIndex: Int): ArrayList<Course> {
        val list = ArrayList<Course>()
        courses.filter {
            try {
                val weekArray = it.week.split('-')
                val startWeek = weekArray[0].toInt()
                val endWeek = weekArray[1].toInt()
                var other = false
                when (it.type) {
                    Constants.COURSE_TYPE_ERROR, Constants.COURSE_TYPE_ALL -> other = true
                    Constants.COURSE_TYPE_SINGLE -> if (weekIndex % 2 == 1)
                        other = true
                    Constants.COURSE_TYPE_DOUBLE -> if (weekIndex % 2 == 0)
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

    fun getTomorrowCourses(courses: Array<Course>): ArrayList<Course> {
        var weekIndex = CalendarUtil.getWeekIndex()//周数
        var dayIndex = CalendarUtil.getWeekIndex()//星期几
        dayIndex++
        if (dayIndex > 7) {
            weekIndex++
            dayIndex %= 7
        }
        return getTodayCourses(courses, weekIndex, dayIndex)
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

    fun mergeCourses(aList: ArrayList<ArrayList<ArrayList<Course>>>, bList: ArrayList<ArrayList<ArrayList<Course>>>): ArrayList<ArrayList<ArrayList<Course>>> {
        if (aList.isEmpty())
            return bList
        if (bList.isEmpty())
            return aList
        val list = ArrayList<ArrayList<ArrayList<Course>>>()
        for (i in 0 until aList.size)
            for (k in 0 until aList[i].size) {
                aList[i][k].addAll(bList[i][k])
            }
        return list
    }

    fun typeMerge(type1: String, type2: String): String {
        if (type1 == Constants.COURSE_TYPE_NOT)
            return type2
        if (type2 == Constants.COURSE_TYPE_NOT)
            return type1
        if (type1 == Constants.COURSE_TYPE_ERROR || type2 == Constants.COURSE_TYPE_ERROR)
            return Constants.COURSE_TYPE_ERROR
        return type1
    }
}