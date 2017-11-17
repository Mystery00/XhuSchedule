package com.weilylab.xhuschedule.util

import java.util.*

/**
 * Created by myste.
 */
object CalendarUtil
{
	private var calendar = Calendar.getInstance()
	var startCalendar = Calendar.getInstance()

	init
	{
		Locale.setDefault(Locale.CHINA)
	}

	fun getWeek(): Int
	{
		calendar = Calendar.getInstance()
		val tempCalendar = Calendar.getInstance()
		tempCalendar.firstDayOfWeek = Calendar.MONDAY
		tempCalendar.timeInMillis = calendar.timeInMillis - startCalendar.timeInMillis
		//获取当前第几周---减一获取正确周数
		return tempCalendar.get(Calendar.WEEK_OF_YEAR) - 1
	}

	fun getWeekIndex(): Int
	{
		calendar = Calendar.getInstance()
		return when (calendar.get(Calendar.DAY_OF_WEEK))
		{
			Calendar.SUNDAY -> 7
			else -> calendar.get(Calendar.DAY_OF_WEEK) - 1
		}
	}
}