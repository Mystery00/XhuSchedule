/*
 * Created by Mystery0 on 17-12-18 下午7:15.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午7:15
 */

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.weilylab.xhuschedule.util.widget.WidgetHelper

class WidgetLastActionService : Service() {
    private var isRun = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isRun)
            return super.onStartCommand(intent, flags, startId)
        Thread(Runnable {
            val tag = intent.getStringExtra("TAG")
            when (tag) {
                WidgetHelper.TABLE_TAG -> {
                    if (WidgetHelper.weekIndex > 0)
                        WidgetHelper.weekIndex--
                    WidgetHelper.refreshWeekCourses(this)
                    sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE")
                            .putExtra("TAG", tag))
                }
                WidgetHelper.TODAY_TAG -> {
                    if (WidgetHelper.dayIndex > 1)
                        WidgetHelper.dayIndex--
                    WidgetHelper.refreshTodayCourses(this)
                    sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE")
                            .putExtra("TAG", tag))
                }
            }
            stopSelf()
        }).start()
        return super.onStartCommand(intent, flags, startId)
    }
}
