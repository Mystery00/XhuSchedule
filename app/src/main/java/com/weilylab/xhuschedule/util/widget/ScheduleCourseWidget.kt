/*
 * Created by Mystery0 on 17-12-18 下午1:58.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-13 下午12:15
 */

package com.weilylab.xhuschedule.util.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.GridWidgetService
import com.weilylab.xhuschedule.service.WidgetLastActionService
import com.weilylab.xhuschedule.service.WidgetNextActionService
import com.weilylab.xhuschedule.util.CalendarUtil
import vip.mystery0.tools.logs.Logs
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class ScheduleCourseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Logs.i(TAG, "onUpdate: ")
        WidgetHelper.saveWidgetIds(context, WidgetHelper.TABLE_TAG, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        Logs.i(TAG, "onEnabled: ")
    }

    override fun onDisabled(context: Context) {
        Logs.i(TAG, "onDisabled: ")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Logs.i(TAG, "onReceive: " + intent.action)
        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE" && intent.getStringExtra("TAG") == WidgetHelper.TABLE_TAG) {
            val appWidgetIds = WidgetHelper.getWidgetIds(context, WidgetHelper.TABLE_TAG)
            Logs.i(TAG, "onReceive: " + WidgetHelper.weekIndex)
            for (appWidgetId in appWidgetIds)
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridView)
        }
    }

    companion object {
        private val TAG = "ScheduleCourseWidget"
        private var hasData = true

        internal fun updateAppWidget(context: Context, appWidgetId: Int) {
            Logs.i(TAG, "updateAppWidget: " + WidgetHelper.weekIndex)
            val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget_course_schedule)
            val lastIntentClick = Intent(context, WidgetLastActionService::class.java)
            lastIntentClick.putExtra("TAG", WidgetHelper.TABLE_TAG)
            val lastPendingIntent = PendingIntent.getService(context, 0, lastIntentClick, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.lastDay, lastPendingIntent)
            val nextIntentClick = Intent(context, WidgetNextActionService::class.java)
            nextIntentClick.putExtra("TAG", WidgetHelper.TABLE_TAG)
            val nextPendingIntent = PendingIntent.getService(context, 0, nextIntentClick, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.nextDay, nextPendingIntent)
            val calendar = Calendar.getInstance()
            remoteViews.setTextViewText(R.id.dateTitle, "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}  ${CalendarUtil.getTodayInfo(context)}")
            val showView = if (hasData) {
                val intent = Intent(context, GridWidgetService::class.java)
                remoteViews.setRemoteAdapter(R.id.gridView, intent)
                remoteViews
            } else {
                RemoteViews(context.packageName, R.layout.layout_widget_no_data)
            }
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, showView)
        }
    }
}

