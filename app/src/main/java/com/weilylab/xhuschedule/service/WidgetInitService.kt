/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午10:32
 */

package com.weilylab.xhuschedule.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import vip.mystery0.tools.logs.Logs

class WidgetInitService : Service() {

    private fun initChannelID(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = context.getString(R.string.notification_channel_id)
            val name = context.getString(R.string.notification_channel_name)
            val description = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.BLUE
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Logs.i("TAG", "onStartCommand: ")
        val builder = NotificationCompat.Builder(this, "Xhu Schedule")
                .setAutoCancel(true)
        val notification = builder.build()
        startForeground(1, notification)

        Thread(Runnable {
            WidgetHelper.refreshWeekCourses(this)
            WidgetHelper.syncDayIndex()
            WidgetHelper.refreshTodayCourses(this)
            sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE")
                    .putExtra("TAG", WidgetHelper.ALL_TAG))
            stopSelf()
        }).start()
        return super.onStartCommand(intent, flags, startId)
    }
}
