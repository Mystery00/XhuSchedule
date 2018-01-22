/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.util.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.weilylab.xhuschedule.APP

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.GridWidgetService
import com.weilylab.xhuschedule.service.WidgetInitService
import com.weilylab.xhuschedule.util.FirebaseUtil
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
        super.onEnabled(context)
        val params = Bundle()
        params.putString(FirebaseUtil.VERSION_NAME, context.getString(R.string.app_version_name))
        params.putString(FirebaseUtil.VERSION_CODE, context.getString(R.string.app_version_code))
        APP.getFirebaseAnalytics().logEvent(FirebaseUtil.ENABLE_WIDGET_SCHEDULE, params)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val params = Bundle()
        params.putString(FirebaseUtil.VERSION_NAME, context.getString(R.string.app_version_name))
        params.putString(FirebaseUtil.VERSION_CODE, context.getString(R.string.app_version_code))
        APP.getFirebaseAnalytics().logEvent(FirebaseUtil.DISABLE_WIDGET_SCHEDULE, params)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE" && (intent.getStringExtra("TAG") == WidgetHelper.TABLE_TAG || intent.getStringExtra("TAG") == WidgetHelper.ALL_TAG)) {
            val appWidgetIds = WidgetHelper.getWidgetIds(context, WidgetHelper.TABLE_TAG)
            for (appWidgetId in appWidgetIds)
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView)
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

