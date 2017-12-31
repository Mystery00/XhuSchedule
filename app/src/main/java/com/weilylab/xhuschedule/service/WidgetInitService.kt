/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午10:32
 */

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import vip.mystery0.tools.logs.Logs

class WidgetInitService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Logs.i("TAG", "onStartCommand: WidgetInitService")
        val builder = NotificationCompat.Builder(this, "Xhu Schedule")
                .setAutoCancel(true)
        val notification = builder.build()
        startForeground(1, notification)

        Thread(Runnable {
            WidgetHelper.refreshWeekCourses(this)
            WidgetHelper.syncDayIndex()
            WidgetHelper.refreshTodayCourses(this)
            WidgetHelper.refreshExamList(this)
            sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE")
                    .putExtra("TAG", WidgetHelper.ALL_TAG))
            stopSelf()
        }).start()
    }
}
