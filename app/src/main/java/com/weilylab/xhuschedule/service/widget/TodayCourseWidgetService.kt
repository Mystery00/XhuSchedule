/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.service.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.repository.WidgetRepository
import com.zhuangfei.timetable.model.Schedule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TodayCourseWidgetService : RemoteViewsService() {
    private val widgetRepository: WidgetRepository by inject()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = ListRemoteViewFactory(this)

    private inner class ListRemoteViewFactory(private val context: Context) : RemoteViewsFactory {
        private val data by lazy { ArrayList<Schedule>() }

        override fun onCreate() {
        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getItemId(position: Int): Long = position.toLong()

        override fun onDataSetChanged() {
            GlobalScope.launch {
                data.clear()
                data.addAll(widgetRepository.queryTodayCourse())
                if (data.isEmpty())
                    sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST)
                            .putExtra("name", TodayCourseWidgetService::class.java.name)
                            .putExtra("hasData", false))
            }
        }

        override fun hasStableIds(): Boolean = true

        override fun getViewAt(position: Int): RemoteViews {
            val remotesView = RemoteViews(context.packageName, R.layout.item_widget_today)
            val course = data[position]
            remotesView.setTextViewText(R.id.course_name_textView, course.name)
            remotesView.setTextViewText(R.id.course_teacher_textView, course.teacher)
            val startTimeArray = context.resources.getStringArray(R.array.start_time)
            val endTimeArray = context.resources.getStringArray(R.array.end_time)
            remotesView.setTextViewText(R.id.course_time_location_textView, "${startTimeArray[course.start - 1]}-${endTimeArray[course.start + course.step - 2]} at ${course.room}")
            remotesView.setInt(R.id.background, "setBackgroundColor", course.extras["colorInt"] as Int)
            return remotesView
        }

        override fun getCount(): Int = data.size

        override fun getViewTypeCount(): Int = 1

        override fun onDestroy() {
            data.clear()
        }
    }
}