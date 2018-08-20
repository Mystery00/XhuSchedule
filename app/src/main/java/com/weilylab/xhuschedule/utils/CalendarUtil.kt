package com.weilylab.xhuschedule.utils

import java.util.*

object CalendarUtil {
	var startDateTime: Calendar = Calendar.getInstance()

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
		calendar.firstDayOfWeek = Calendar.MONDAY
		return when (calendar.get(Calendar.DAY_OF_WEEK)) {
			Calendar.SUNDAY -> 7
			else -> calendar.get(Calendar.DAY_OF_WEEK) - 1
		}
	}

	fun getWeekIndexInString(index: Int = getWeekIndex()): String {
		val weeks = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
		return weeks[index - 1]
	}

	fun getSelectArray(grade: String?): Array<String> {
		val calendar = Calendar.getInstance()
		val nowYear = calendar.get(Calendar.YEAR)
		val month = calendar.get(Calendar.MONTH)
		val startYear = grade?.toInt() ?: nowYear-3
		val endYear = if (month >= Calendar.SEPTEMBER) nowYear + 1 else nowYear
		return Array(endYear - startYear) { i -> "${startYear + i}-${startYear + 1 + i}" }
	}
}