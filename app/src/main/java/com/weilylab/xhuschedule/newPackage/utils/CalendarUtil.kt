package com.weilylab.xhuschedule.newPackage.utils

import java.util.*

object CalendarUtil {
	fun getWeekFromCalendar(startDateTime: Calendar): Int {
		val now = Calendar.getInstance()
		now.set(Calendar.HOUR_OF_DAY, 0)
		now.set(Calendar.MINUTE, 0)
		now.set(Calendar.SECOND, 0)
		val seconds = now.timeInMillis / 1000 - startDateTime.timeInMillis / 1000
		return (seconds / 60 / 60 / 24 / 7).toInt() + 1
	}

	fun getWeekIndex(): Int {
		val calendar = Calendar.getInstance()
		return when (calendar.get(Calendar.DAY_OF_WEEK)) {
			Calendar.SUNDAY -> 7
			else -> calendar.get(Calendar.DAY_OF_WEEK) - 1
		}
	}
}