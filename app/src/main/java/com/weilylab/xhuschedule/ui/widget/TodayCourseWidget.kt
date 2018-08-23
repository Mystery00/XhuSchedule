package com.weilylab.xhuschedule.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import com.weilylab.xhuschedule.service.widget.TodayCourseWidgetService
import com.weilylab.xhuschedule.service.widget.WidgetUpdateService
import com.weilylab.xhuschedule.utils.WidgetUtil
import vip.mystery0.logs.Logs

class TodayCourseWidget : AppWidgetProvider() {
	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		Logs.i("onUpdate: ")
		WidgetUtil.saveWidgetIDs(context, SharedPreferenceConstant.FIELD_IDS_TODAY, appWidgetIds)
		ContextCompat.startForegroundService(context, Intent(context, WidgetUpdateService::class.java))
	}

	override fun onReceive(context: Context, intent: Intent?) {
		super.onReceive(context, intent)
		Logs.i("onReceive: ${intent?.action}")
		if (intent?.action == Constants.ACTION_WIDGET_UPDATE_BROADCAST) {
			val appWidgetIds = WidgetUtil.getWidgetIDs(context, SharedPreferenceConstant.FIELD_IDS_TODAY)
			appWidgetIds.forEach {
				updateAppWidget(context, AppWidgetManager.getInstance(context), it)
			}
		}
	}

	override fun onDisabled(context: Context?) {
		super.onDisabled(context)
		context?.stopService(Intent(context, WidgetUpdateService::class.java))
	}

	private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
		val views = RemoteViews(context.packageName, R.layout.today_course_widget)
		val intent = Intent(context, TodayCourseWidgetService::class.java)
		views.setRemoteAdapter(R.id.listView, intent)
		appWidgetManager.updateAppWidget(appWidgetId, views)
	}
}

