package com.weilylab.xhuschedule.util

import java.util.*

/**
 * Created by myste.
 */
object CalendarUtil {
    private var calendar = Calendar.getInstance()
    var startCalendar = Calendar.getInstance()

    init {
        Locale.setDefault(Locale.CHINA)
    }

    fun getWeek(): Int {
        calendar = Calendar.getInstance()
        var days = calendar.get(Calendar.DAY_OF_YEAR) - startCalendar.get(Calendar.DAY_OF_YEAR)
        val nowYear = calendar.get(Calendar.YEAR)
        val tempCalendar = startCalendar.clone() as Calendar
        if (tempCalendar.get(Calendar.YEAR) != nowYear)
            do {
                days += tempCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)
                tempCalendar.add(Calendar.YEAR, 1)
            } while (tempCalendar.get(Calendar.YEAR) != nowYear)
        //获取当前第几周
        return days / 7 + 1
    }

    fun getWeekIndex(): Int {
        calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> 7
            else -> calendar.get(Calendar.DAY_OF_WEEK) - 1
        }
    }
}