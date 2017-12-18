/*
 * Created by Mystery0 on 17-12-18 下午1:58.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-13 下午12:15
 */

package com.weilylab.xhuschedule.util.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.GridWidgetService
import vip.mystery0.tools.logs.Logs

/**
 * Implementation of App Widget functionality.
 */
class ScheduleCourseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Logs.i(TAG, "onUpdate: ")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Logs.i(TAG, "onEnabled: ")
        hasData = WidgetHelper.checkWeekCache(context)
    }

    override fun onDisabled(context: Context) {
        Logs.i(TAG, "onDisabled: ")
    }

    companion object {
        private val TAG = "ScheduleCourseWidget"
        private var hasData = true

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            val view = if (hasData) {
                val views = RemoteViews(context.packageName, R.layout.layout_widget_course_schedule)
                val intent = Intent(context, GridWidgetService::class.java)
                views.setRemoteAdapter(R.id.gridView, intent)
                views
            } else {
                RemoteViews(context.packageName, R.layout.layout_widget_no_data)
            }
            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}

