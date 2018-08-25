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

	fun getTomorrowWeekFromCalendar(startDateTime: Calendar): Int {
		val todayWeek = getWeekFromCalendar(startDateTime)
		return if (getWeekIndex() == 7) todayWeek + 1 else todayWeek
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
			now.add(Calendar.DATE, 1)
			(year == now.get(Calendar.YEAR)) && (month == now.get(Calendar.MONTH) + 1) && (day == now.get(Calendar.DATE))
		} catch (e: Exception) {
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

	fun getNotificationTime(): Long {
		val now = Calendar.getInstance()
		val calendar = Calendar.getInstance()
		val setTime = ConfigurationUtil.notificationTime.split(':')
		calendar.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), setTime[0].toInt(), setTime[1].toInt(), 0)
		if (calendar.timeInMillis < now.timeInMillis)
			calendar.add(Calendar.DATE, 1)
		return calendar.timeInMillis
	}

	fun whenBeginSchool(): Int {
		val calendar = Calendar.getInstance()
		startDateTime.set(Calendar.HOUR_OF_DAY, 0)
		startDateTime.set(Calendar.MINUTE, 0)
		startDateTime.set(Calendar.SECOND, 0)
		startDateTime.set(Calendar.MILLISECOND, 0)
		if (startDateTime.timeInMillis <= calendar.timeInMillis)
			return 0
		calendar.set(Calendar.HOUR_OF_DAY, 0)
		calendar.set(Calendar.MINUTE, 0)
		calendar.set(Calendar.SECOND, 0)
		calendar.set(Calendar.MILLISECOND, 0)
		var num = 0
		while (calendar.timeInMillis < startDateTime.timeInMillis) {
			calendar.add(Calendar.DATE, 1)
			num++
		}
		return num
	}

	fun getTrueWeek(): Int {
		val calendar = Calendar.getInstance()
		calendar.set(Calendar.HOUR_OF_DAY, 0)
		calendar.set(Calendar.MINUTE, 0)
		calendar.set(Calendar.SECOND, 0)
		calendar.set(Calendar.MILLISECOND, 0)
		startDateTime.set(Calendar.HOUR_OF_DAY, 0)
		startDateTime.set(Calendar.MINUTE, 0)
		startDateTime.set(Calendar.SECOND, 0)
		startDateTime.set(Calendar.MILLISECOND, 0)
		return ((calendar.timeInMillis - startDateTime.timeInMillis) / 1000 / 60 / 60 / 24 / 7).toInt()
	}

	fun getDateStringFromWeek(curWeek: Int, targetWeek: Int): List<String> {
		val calendar = Calendar.getInstance()
		if (targetWeek == curWeek)
			return getDateStringFromCalendar(calendar)
		val amount = targetWeek - curWeek
		calendar.add(Calendar.WEEK_OF_YEAR, amount)
		return getDateStringFromCalendar(calendar)
	}

	private fun getDateStringFromCalendar(calendar: Calendar): List<String> {
		val dateList = ArrayList<String>()
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.add(Calendar.DAY_OF_MONTH, -1)
		}
		calendar.firstDayOfWeek = Calendar.MONDAY
		dateList.add("${calendar.get(Calendar.MONTH) + 1}月")
		for (i in 0..6) {
			if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
				dateList.add("${calendar.get(Calendar.MONTH) + 1}月")
			else
				dateList.add("${calendar.get(Calendar.DAY_OF_MONTH)}日")
			calendar.add(Calendar.DAY_OF_MONTH, 1)
		}
		return dateList
	}
}