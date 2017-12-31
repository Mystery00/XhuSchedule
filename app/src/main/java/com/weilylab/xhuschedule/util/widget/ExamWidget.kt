/*
 * Created by Mystery0 on 17-12-31 下午5:14.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-31 下午5:14
 */

package com.weilylab.xhuschedule.util.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.ExamWidgetService
import com.weilylab.xhuschedule.service.WidgetInitService
import com.weilylab.xhuschedule.util.CalendarUtil

/**
 * Implementation of App Widget functionality.
 */
class ExamWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        WidgetHelper.saveWidgetIds(context, WidgetHelper.EXAM_TAG, appWidgetIds)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(Intent(context, WidgetInitService::class.java))
        else
            context.startService(Intent(context, WidgetInitService::class.java))
        for (appWidgetId in appWidgetIds)
            TodayCourseWidget.updateAppWidget(context, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE" && (intent.getStringExtra("TAG") == WidgetHelper.EXAM_TAG || intent.getStringExtra("TAG") == WidgetHelper.ALL_TAG)) {
            val appWidgetIds = WidgetHelper.getWidgetIds(context, WidgetHelper.EXAM_TAG)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetId)
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView)
            }
        }
    }

    companion object {
        internal fun updateAppWidget(context: Context, appWidgetId: Int) {
            val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget_course_exam)
            remoteViews.setTextViewText(R.id.dateTitle, CalendarUtil.formatInfo(context))
            val intent = Intent(context, ExamWidgetService::class.java)
            remoteViews.setRemoteAdapter(R.id.listView, intent)
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews)
        }
    }
}

