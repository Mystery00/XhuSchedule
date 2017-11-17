package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.CourseTimeInfo
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
object CourseUtil
{
	private val TAG = "CourseUtil"

	@JvmStatic
	fun formatCourses(courses: Array<Course>): ArrayList<Course?>
	{
		val tempArray = Array(5, { Array<Course?>(7, { null }) })
		courses.forEach {
			val timeArray = it.time.split('-')
			val startTime = (timeArray[0].toInt() - 1) / 2
			val endTime = (timeArray[1].toInt()) / 2
			for (index in startTime until endTime)
			{
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

	@JvmStatic
	fun getWeekCourses(courses: Array<Course>): ArrayList<Course?>
	{
		val calendarUtil = CalendarUtil.getInstance()
		//开学时间
		calendarUtil.startCalendar.set(2017, 8, 4, 0, 0, 0)//月数减一
		val currentWeek = calendarUtil.getWeek()
		val tempArray = Array(5, { Array<Course?>(7, { null }) })
		courses.filter {
			try
			{
				var other = false
				when (it.type)
				{
					"0" -> other = true
					"1" -> if (currentWeek % 2 == 1)
						other = true
					"2" -> if (currentWeek % 2 == 0)
						other = true
					else -> other = false
				}
				val weekArray = it.week.split('-')
				val startWeek = weekArray[0].toInt()
				val endWeek = weekArray[1].toInt()
				currentWeek in startWeek..endWeek && other
			}
			catch (e: Exception)
			{
				false
			}
		}.forEach {
			val timeArray = it.time.split('-')
			val startTime = (timeArray[0].toInt() - 1) / 2
			val endTime = (timeArray[1].toInt()) / 2
			for (index in startTime until endTime)
			{
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

	@JvmStatic
	fun getTodayCourses(courses: Array<Course>): ArrayList<Course>
	{
		val calendarUtil = CalendarUtil.getInstance()
		//开学时间
		calendarUtil.startCalendar.set(2017, 8, 4, 0, 0, 0)//月数减一
		//获取当前第几周
		val currentWeek = calendarUtil.getWeek()
		val weekIndex = calendarUtil.getWeekIndex()
		val list = ArrayList<Course>()
		courses.filter {
			try
			{
				val weekArray = it.week.split('-')
				val startWeek = weekArray[0].toInt()
				val endWeek = weekArray[1].toInt()
				var other = false
				when (it.type)
				{
					"0" -> other = true
					"1" -> if (currentWeek % 2 == 1)
						other = true
					"2" -> if (currentWeek % 2 == 0)
						other = true
					else -> other = false
				}
				currentWeek in startWeek..endWeek && other && (it.day.toInt()) == weekIndex
			}
			catch (e: Exception)
			{
				false
			}
		}
				.forEach {
					list.add(it)
				}
		return list
	}

	@JvmStatic
	fun splitInfo(course: Course): Array<CourseTimeInfo>
	{
		val array = course.location.split('\n')
		return if (array.size > 1)
		{
			Array(array.size, { i ->
				Logs.i(TAG, "splitInfo: " + array[i])
				val info = array[i].replace('(', ' ').replace(')', ' ').split(' ')
				CourseTimeInfo(info[1], info[0])
			})
		}
		else
			arrayOf(CourseTimeInfo(course.week, course.location))
	}
}