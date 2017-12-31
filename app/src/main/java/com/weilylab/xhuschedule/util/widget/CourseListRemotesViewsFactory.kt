/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午9:17
 */

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings

/**
 * Created by mystery0.
 */
class CourseListRemotesViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        return if (WidgetHelper.showTodayCourses.size != 0) {
            val course = WidgetHelper.showTodayCourses[position]
            val remotesView = RemoteViews(context.packageName, R.layout.item_widget_today)
            remotesView.setTextViewText(R.id.course_name, course.name)
            remotesView.setTextViewText(R.id.course_teacher, course.teacher)
            try {
                val startTime = context.resources.getStringArray(R.array.start_time)
                val endTime = context.resources.getStringArray(R.array.end_time)
                val time = course.time.trim().split("-")
                val showTime = context.getString(R.string.course_time_format, startTime[time[0].toInt() - 1], endTime[time[1].toInt() - 1])
                remotesView.setTextViewText(R.id.course_time_location, "$showTime at ${course.location}")
            } catch (e: Exception) {
                e.printStackTrace()
                remotesView.setTextViewText(R.id.course_time_location, "${course.time} at ${course.location}")
            }
            try {
                remotesView.setInt(R.id.background, "setBackgroundColor", Color.parseColor('#' + Integer.toHexString(Settings.customTodayOpacity) + course.color.substring(1)))
            } catch (e: Exception) {
                remotesView.setInt(R.id.background, "setBackgroundColor", Color.parseColor('#' + Integer.toHexString(Settings.customTodayOpacity) + ScheduleHelper.getRandomColor()))
            }
            remotesView
        } else {
            RemoteViews(context.packageName, R.layout.layout_widget_no_course)
        }
    }

    override fun getCount(): Int {
        return if (WidgetHelper.showTodayCourses.size != 0) WidgetHelper.showTodayCourses.size else 1
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun onDestroy() {
    }
}