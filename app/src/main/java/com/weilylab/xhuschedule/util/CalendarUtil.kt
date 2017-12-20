/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-24 下午3:14
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import vip.mystery0.tools.logs.Logs
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
        return getWeek(getDay())
    }

    fun getWeek(dayIndex: Int): Int {
        //获取当前第几周
        return dayIndex / 7 + 1
    }

    fun getDay(): Int {
        calendar = Calendar.getInstance()
        var days = calendar.get(Calendar.DAY_OF_YEAR) - startCalendar.get(Calendar.DAY_OF_YEAR)
        val nowYear = calendar.get(Calendar.YEAR)
        val tempCalendar = startCalendar.clone() as Calendar
        if (tempCalendar.get(Calendar.YEAR) != nowYear)
            do {
                days += tempCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)
                tempCalendar.add(Calendar.YEAR, 1)
            } while (tempCalendar.get(Calendar.YEAR) != nowYear)
        return days
    }

    fun formatInfo(context: Context): String {
        val firstWeekOfTerm = Settings.firstWeekOfTerm
        val date = firstWeekOfTerm.split('-')
        val calendar = Calendar.getInstance()
        calendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
        calendar.add(Calendar.DAY_OF_YEAR, WidgetHelper.dayIndex)
        return "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}  ${CalendarUtil.getTodayInfo(context, WidgetHelper.dayIndex)}"
    }

    fun getWeekIndex(): Int {
        calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> 7
            else -> calendar.get(Calendar.DAY_OF_WEEK) - 1
        }
    }

    private fun getTodayInfo(context: Context, dayIndex: Int): String {
        val week = getWeek(dayIndex)
        val day = dayIndex % 7 + 1
        val weekArray = context.resources.getStringArray(R.array.table_header)
        return context.getString(R.string.course_today_info, week, weekArray[day - 1])
    }

    fun getTodayInfo(context: Context): String {
        val week = getWeek()
        val day = getWeekIndex()
        val weekArray = context.resources.getStringArray(R.array.table_header)
        return context.getString(R.string.course_today_info, week, weekArray[day - 1])
    }

    fun showDate(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return calendar.time.toString()
    }
}