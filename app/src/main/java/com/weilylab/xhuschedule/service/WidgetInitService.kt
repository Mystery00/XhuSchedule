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
import android.support.v4.app.NotificationManagerCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.widget.WidgetHelper

class WidgetInitService : Service() {
    companion object {
        private val NOTIFICATION_ID = 0
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "Xhu Schedule")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("正在初始化数据")
                .setAutoCancel(true)
                .setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
                .build()
        startForeground(NOTIFICATION_ID, notification)

        Thread(Runnable {
            WidgetHelper.refreshWeekCourses(this)
            WidgetHelper.syncDayIndex()
            WidgetHelper.refreshTodayCourses(this)
            WidgetHelper.refreshExamList(this)
            sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE")
                    .putExtra("TAG", WidgetHelper.ALL_TAG))
            stopSelf()
        }).start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }
}
