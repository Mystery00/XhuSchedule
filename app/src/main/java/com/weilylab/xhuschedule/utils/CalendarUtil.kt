package com.weilylab.xhuschedule.utils

import com.weilylab.xhuschedule.model.Test
import vip.mystery0.logs.Logs
import java.text.SimpleDateFormat
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

	fun getTomorrowIndex(): Int {
		val nowIndex = getWeekIndex()
		return when (nowIndex) {
			7 -> 1
			else -> nowIndex + 1
		}
	}

	fun isTomorrowTest(dateString: String): Boolean {
		if (dateString == "")
			return false
		return try {
			val dateArray = dateString.split('-')
			val year = dateArray[0].toInt()
			val month = dateArray[1].toInt()
			val day = dateArray[2].toInt()
			val now = Calendar.getInstance()
			now.set(Calendar.HOUR_OF_DAY, 0)
			now.set(Calendar.MINUTE, 0)
			now.set(Calendar.SECOND, 0)
			year == now.get(Calendar.YEAR) && month == now.get(Calendar.MONTH) + 1 && day == now.get(Calendar.DATE)
		} catch (e: Exception) {
			Logs.wtf("isTomorrowTest: ", e)
			false
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

	fun getFormattedText(): String {
		val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
		return "${simpleDateFormat.format(Calendar.getInstance().time)} ${getWeekIndexInString()}"
	}

	fun getTestDateText(test: Test): String {
		if (test.date == "")
			return ""
		try {
			val dayArray = test.date.split('-')
			val startTimeArray = test.time.split('-')[0].split(':')
			val endTimeArray = test.time.split('-')[1].split(':')
			val startCalendar = Calendar.getInstance()
			startCalendar.set(dayArray[0].toInt(), dayArray[1].toInt() - 1, dayArray[2].toInt(), startTimeArray[0].toInt(), startTimeArray[1].toInt(), 0)
			val endCalendar = Calendar.getInstance()
			endCalendar.set(dayArray[0].toInt(), dayArray[1].toInt() - 1, dayArray[2].toInt(), endTimeArray[0].toInt(), endTimeArray[1].toInt(), 0)
			val nowCalendar = Calendar.getInstance()
			return when {
				nowCalendar.timeInMillis < startCalendar.timeInMillis -> {
					val millis = startCalendar.timeInMillis - nowCalendar.timeInMillis
					if (millis > 1000 * 60 * 60 * 24)//大于一天
						"${millis / 1000 / 60 / 60 / 24} 天后"
					else//小时
						"${millis / 1000 / 60 / 60} 小时后"
				}
				nowCalendar.timeInMillis > endCalendar.timeInMillis ->
					"已考过"
				else -> "考试中"
			}
		} catch (e: Exception) {
			Logs.wtf("getTestDateText: ", e)
			return ""
		}
	}
}