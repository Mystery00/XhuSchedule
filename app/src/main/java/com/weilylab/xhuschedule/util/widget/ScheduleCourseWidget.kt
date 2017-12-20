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
import com.weilylab.xhuschedule.service.WidgetInitService
import vip.mystery0.tools.logs.Logs

/**
 * Implementation of App Widget functionality.
 */
class ScheduleCourseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Logs.i(TAG, "onUpdate: ")
        WidgetHelper.saveWidgetIds(context, WidgetHelper.TABLE_TAG, appWidgetIds)
        context.startService(Intent(context, WidgetInitService::class.java))
        for (appWidgetId in appWidgetIds)
            updateAppWidget(context, appWidgetId)
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE" && (intent.getStringExtra("TAG") == WidgetHelper.TABLE_TAG || intent.getStringExtra("TAG") == WidgetHelper.ALL_TAG)) {
            val appWidgetIds = WidgetHelper.getWidgetIds(context, WidgetHelper.TABLE_TAG)
            for (appWidgetId in appWidgetIds)
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridView)
        }
    }

    companion object {
        private val TAG = "ScheduleCourseWidget"

        internal fun updateAppWidget(context: Context, appWidgetId: Int) {
            val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget_course_schedule)
            val intent = Intent(context, GridWidgetService::class.java)
            remoteViews.setRemoteAdapter(R.id.listView, intent)
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews)
        }
    }
}

