/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午5:08
 */

package com.weilylab.xhuschedule.util.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.TodayWidgetService
import com.weilylab.xhuschedule.service.WidgetInitService
import com.weilylab.xhuschedule.service.WidgetLastActionService
import com.weilylab.xhuschedule.service.WidgetNextActionService
import com.weilylab.xhuschedule.util.CalendarUtil

/**
 * Implementation of App Widget functionality.
 */
class TodayCourseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        WidgetHelper.saveWidgetIds(context, WidgetHelper.TODAY_TAG, appWidgetIds)
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
        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE" && (intent.getStringExtra("TAG") == WidgetHelper.TODAY_TAG || intent.getStringExtra("TAG") == WidgetHelper.ALL_TAG)) {
            val appWidgetIds = WidgetHelper.getWidgetIds(context, WidgetHelper.TODAY_TAG)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetId)
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView)
            }
        }
    }

    companion object {
        internal fun updateAppWidget(context: Context, appWidgetId: Int) {
            val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget_course_today)
            val lastIntentClick = Intent(context, WidgetLastActionService::class.java)
            lastIntentClick.putExtra("TAG", WidgetHelper.TODAY_TAG)
            val lastPendingIntent = PendingIntent.getService(context, 0, lastIntentClick, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.lastDay, lastPendingIntent)
            val nextIntentClick = Intent(context, WidgetNextActionService::class.java)
            nextIntentClick.putExtra("TAG", WidgetHelper.TODAY_TAG)
            val nextPendingIntent = PendingIntent.getService(context, 0, nextIntentClick, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.nextDay, nextPendingIntent)
            remoteViews.setTextViewText(R.id.dateTitle, CalendarUtil.formatInfo(context))
            val intent = Intent(context, TodayWidgetService::class.java)
            remoteViews.setRemoteAdapter(R.id.listView, intent)
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews)
        }
    }
}

