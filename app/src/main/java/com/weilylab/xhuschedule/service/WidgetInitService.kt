/*
 * Created by Mystery0 on 17-12-18 下午7:27.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午7:27
 */

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.weilylab.xhuschedule.util.widget.WidgetHelper

class WidgetInitService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Thread(Runnable {
            WidgetHelper.syncWeekIndex()
            WidgetHelper.syncDayIndex()
            WidgetHelper.refreshTodayCourses(this)
            WidgetHelper.refreshWeekCourses(this)
            sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE"))
            stopSelf()
        }).start()
    }
}
