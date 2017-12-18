/*
 * Created by Mystery0 on 17-12-18 下午1:59.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午1:59
 */

package com.weilylab.xhuschedule.util.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.TodayWidgetService
import com.weilylab.xhuschedule.util.CalendarUtil
import vip.mystery0.tools.logs.Logs
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class TodayCourseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Logs.i(TAG, "onUpdate: ")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Logs.i(TAG, "onEnabled: ")

    }

    override fun onDisabled(context: Context) {
        Logs.i(TAG, "onDisabled: ")
    }

    companion object {
        private val TAG = "TodayCourseWidget"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.layout_widget_course_today)
            val calendar = Calendar.getInstance()
            views.setTextViewText(R.id.dateTitle, "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}  ${CalendarUtil.getTodayInfo(context)}")
            val intent = Intent(context, TodayWidgetService::class.java)
            views.setRemoteAdapter(R.id.listView, intent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

