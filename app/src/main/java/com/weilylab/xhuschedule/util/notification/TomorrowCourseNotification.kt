/*
 * Created by Mystery0 on 18-2-26 下午4:40.
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
 * Last modified 18-2-26 下午4:40
 */

package com.weilylab.xhuschedule.util.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.Settings

object TomorrowCourseNotification {
    private const val NOTIFICATION_TAG = "TomorrowCourse"

    fun notify(context: Context, id: Int, courseList: ArrayList<Course>) {
        val title = context.getString(R.string.tomorrow_course_notification_title, courseList.size)
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_TOMORROW)
                .setSound(Uri.parse(Settings.notificationSound))
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_tomorrow)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.tomorrow_info_notification_placeholder_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                context.packageManager.getLaunchIntentForPackage(context.packageName),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
        if (Settings.notificationVibrate) {
            val vibrate = longArrayOf(0, 1000)
            builder.setVibrate(vibrate)
        }
        val style = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
        courseList.forEach {
            val courseItem = SpannableStringBuilder()
            courseItem.append(it.name)
            courseItem.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, courseItem.length, 0)
            courseItem.append("\t上课时间：${it.time} 上课地点：${it.location}")
            style.addLine(courseItem)
        }
        builder.setStyle(style)

        notify(context, id, builder.build())
    }

    private fun notify(context: Context, id: Int, notification: Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_TAG, id, notification)
    }

    fun cancel(context: Context, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_TAG, id)
    }
}
