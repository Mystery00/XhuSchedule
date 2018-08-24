package com.weilylab.xhuschedule.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.service.widget.TodayCourseWidgetService
import com.weilylab.xhuschedule.service.widget.WidgetUpdateService
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.WidgetUtil
import com.weilylab.xhuschedule.viewModel.WidgetViewModelHelper
import vip.mystery0.logs.Logs

class TodayCourseWidget : AppWidgetProvider() {
	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		WidgetUtil.saveWidgetIDs(context, SharedPreferenceConstant.FIELD_IDS_TODAY, appWidgetIds)
		ContextCompat.startForegroundService(context, Intent(context, WidgetUpdateService::class.java))
	}

	override fun onReceive(context: Context, intent: Intent?) {
		super.onReceive(context, intent)
		if (intent?.action == Constants.ACTION_WIDGET_UPDATE_BROADCAST)
			WidgetUtil.getWidgetIDs(context, SharedPreferenceConstant.FIELD_IDS_TODAY).forEach {
				updateAppWidget(context, AppWidgetManager.getInstance(context), it)
			}
	}

	override fun onDisabled(context: Context?) {
		super.onDisabled(context)
		context?.stopService(Intent(context, WidgetUpdateService::class.java))
	}

	private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
		val views = RemoteViews(context.packageName, R.layout.today_course_widget)
		views.setTextViewText(R.id.appwidget_text, CalendarUtil.getFormattedText())
		val refreshIntent = Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST)
		val refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		views.setOnClickPendingIntent(R.id.appwidget_text, refreshPendingIntent)
		if (WidgetViewModelHelper.todayCourseList.value == null || WidgetViewModelHelper.todayCourseList.value!!.data == null || WidgetViewModelHelper.todayCourseList.value!!.data!!.isEmpty()) {
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

