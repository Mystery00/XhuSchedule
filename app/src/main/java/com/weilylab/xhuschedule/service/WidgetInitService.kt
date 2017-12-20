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
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import vip.mystery0.tools.logs.Logs

class WidgetInitService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Logs.i("TAG", "onStartCommand: ")
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
