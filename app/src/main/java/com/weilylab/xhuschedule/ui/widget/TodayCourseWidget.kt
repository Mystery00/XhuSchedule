package com.weilylab.xhuschedule.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.service.WidgetUpdateService
import com.weilylab.xhuschedule.service.widget.TodayCourseWidgetService
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.WidgetUtil

class TodayCourseWidget : AppWidgetProvider() {
	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		WidgetUtil.saveWidgetIDs(context, SharedPreferenceConstant.FIELD_IDS_TODAY, appWidgetIds)
		appWidgetIds.forEach { updateAppWidget(context, appWidgetManager, it) }
	}

	override fun onReceive(context: Context, intent: Intent?) {
		super.onReceive(context, intent)
		if (intent?.action == Constants.ACTION_WIDGET_UPDATE_BROADCAST) {
			val appWidgetManager = AppWidgetManager.getInstance(context)
			if (intent.getStringExtra("name") == TodayCourseWidgetService::class.java.name && intent.hasExtra("hasData") && !intent.getBooleanExtra("hasData", true)) {
				WidgetUtil.getWidgetIDs(context, SharedPreferenceConstant.FIELD_IDS_TODAY)
						.forEach { updateAppWidget(context, appWidgetManager, it, false) }
			} else {
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(ComponentName(context, TodayCourseWidget::class.java)), R.id.listView)
			}
		}
	}

	private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, hasData: Boolean = true) {
		val views = RemoteViews(context.packageName, R.layout.today_course_widget)
		views.setTextViewText(R.id.appwidget_text, CalendarUtil.getFormattedText())
		val refreshIntent = Intent(context, WidgetUpdateService::class.java)
		val refreshPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			PendingIntent.getForegroundService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		else
			PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		views.setOnClickPendingIntent(R.id.appwidget_text, refreshPendingIntent)
		if (!hasData) {
			views.setViewVisibility(R.id.listView, View.GONE)
			views.setViewVisibility(R.id.nullDataView, View.VISIBLE)
		} else {
			views.setViewVisibility(R.id.listView, View.VISIBLE)
			views.setViewVisibility(R.id.nullDataView, View.GONE)
			val intent = Intent(context, TodayCourseWidgetService::class.java)
			views.setRemoteAdapter(R.id.listView, intent)
		}
		appWidgetManager.updateAppWidget(appWidgetId, views)
	}
}

