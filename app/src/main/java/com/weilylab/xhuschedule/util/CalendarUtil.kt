package com.weilylab.xhuschedule.util

import java.util.*

/**
 * Created by myste.
 */
class CalendarUtil private constructor()
{
	companion object
	{
		private var calendarUtil: CalendarUtil? = null

		fun getInstance(): CalendarUtil
		{
			if (calendarUtil == null)
				calendarUtil = CalendarUtil()
			return calendarUtil!!
		}
	}

	private val calendar = Calendar.getInstance()
	var startCalendar = Calendar.getInstance()

	init
	{
		Locale.setDefault(Locale.CHINA)
	}

	fun getWeek(): Int
	{
		val tempCalendar = Calendar.getInstance()
		tempCalendar.timeInMillis = calendar.timeInMillis - startCalendar.timeInMillis
		//获取当前第几周---加一获取正确周数
		return tempCalendar.get(Calendar.WEEK_OF_YEAR) - 1
	}

	fun getWeekIndex(): Int
	{
		return when (calendar.get(Calendar.DAY_OF_WEEK))
		{
			Calendar.SUNDAY -> 7
			else -> calendar.get(Calendar.DAY_OF_WEEK) - 1
		}
	}
}