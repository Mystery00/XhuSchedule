/*
 * Created by Mystery0 on 17-12-5 下午3:40.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-5 下午3:40
 */

package com.weilylab.xhuschedule.util

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
class CourseWidget : AppWidgetProvider() {

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
        private val TAG = "CourseWidget"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.course_widget)
            val intent=Intent(context,GridWidgetService::class.java)
            views.setRemoteAdapter(R.id.gridView,intent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

