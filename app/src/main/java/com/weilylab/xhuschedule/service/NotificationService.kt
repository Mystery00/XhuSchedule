/*
 * Created by Mystery0 on 18-2-21 下午9:25.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:25
 */

package com.weilylab.xhuschedule.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.weilylab.xhuschedule.receiver.AlarmReceiver
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.Settings
import vip.mystery0.tools.logs.Logs

class NotificationService : Service() {
    private val TAG = "NotificationService"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        alarmManager.cancel(pendingIntent)//关闭定时器
        if (!Settings.isNotificationTomorrowEnable && Settings.isNotificationExamEnable) {
            Logs.i(TAG, "onStartCommand: 关闭服务")
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        val triggerAtTime = CalendarUtil.getNotificationTriggerTime()
        if (Settings.notificationExactTime)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent)
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent)
        return super.onStartCommand(intent, flags, startId)
    }
}
