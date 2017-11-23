package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.CourseTimeInfo

/**
 * Created by myste.
                        */
                    object CourseUtil {
                        fun formatCourses(courses: Array<Course>): ArrayList<Course?> {
                            val tempArray = Array(5, { Array<Course?>(7, { null }) })
                            courses.forEach {
                                val timeArray = it.time.split('-')
                                val startTime = (timeArray[0].toInt() - 1) / 2
                                val endTime = (timeArray[1].toInt()) / 2
                                for (index in startTime until endTime) {
                                    if (tempArray[index][it.day.toInt() - 1] == null)
                    tempArray[index][it.day.toInt() - 1] = it
                else
                    tempArray[index][it.day.toInt() - 1]?.with(it)
            }
        }
        val list = ArrayList<Course?>()
        tempArray.forEach {
            it.forEach {
                list.add(it)
            }
        }
        return list
    }

    fun getWeekCourses(courses: Array<Course>): ArrayList<Course?> {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        CalendarUtil.startCalendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        val currentWeek = CalendarUtil.getWeek()
        ScheduleHelper.weekIndex = currentWeek
        return getWeekCourses(courses, currentWeek)
    }

    fun getWeekCourses(courses: Array<Course>, weekIndex: Int): ArrayList<Course?> {
        ScheduleHelper.weekIndex = weekIndex
        val tempArray = Array(5, { Array<Course?>(7, { null }) })
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
                if (tempArray[index][it.day.toInt() - 1] == null)
                    tempArray[index][it.day.toInt() - 1] = it
                else
                    tempArray[index][it.day.toInt() - 1]?.with(it)
            }
        }
        val list = ArrayList<Course?>()
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
}