/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.ColorPoolHelper
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.CustomThing
import com.weilylab.xhuschedule.model.Test
import com.weilylab.xhuschedule.ui.activity.QueryTestActivity
import com.zhuangfei.timetable.model.Schedule

object TomorrowNotification {
    private const val NOTIFICATION_TAG = "TomorrowNotification"

    fun notifyCustomThing(context: Context, notificationManager: NotificationManager, customThingList: List<CustomThing>) {
        if (customThingList.isEmpty()) {
            cancel(notificationManager, Constants.NOTIFICATION_ID_TOMORROW_CUSTOM_THING)
            return
        }
        val title = "您明天有${customThingList.size}件事项哦~"
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_TOMORROW)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_stat_init)
                .setContentTitle(title)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(PendingIntent.getActivity(context, 0, context.packageManager.getLaunchIntentForPackage(context.packageName), PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
        val style = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
        customThingList.forEach {
            val item = SpannableStringBuilder()
            item.append(it.title)
            item.setSpan(ForegroundColorSpan(Color.parseColor(it.color)), 0, item.length, 0)
            item.appendLine("  ${it.startTime} - ${it.endTime} at ${it.location}")
            style.addLine(item)
        }
        style.addLine("具体详情请点击查看")
        builder.setStyle(style)
        notify(notificationManager, Constants.NOTIFICATION_ID_TOMORROW_CUSTOM_THING, builder.build())
    }

    fun notifyCourse(context: Context, notificationManager: NotificationManager, courseList: List<Schedule>) {
        if (courseList.isEmpty()) {
            cancel(notificationManager, Constants.NOTIFICATION_ID_TOMORROW_COURSE)
            return
        }
        val title = "您明天有${courseList.size}节课要上哦~"
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_TOMORROW)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_stat_init)
                .setContentTitle(title)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(PendingIntent.getActivity(context, 0, context.packageManager.getLaunchIntentForPackage(context.packageName), PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
        val style = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
        val startTimeArray = context.resources.getStringArray(R.array.start_time)
        val endTimeArray = context.resources.getStringArray(R.array.end_time)
        courseList.forEach {
            val courseItem = SpannableStringBuilder()
            courseItem.append(it.name)
            courseItem.setSpan(ForegroundColorSpan(ColorPoolHelper.colorPool.getColorAuto(it.colorRandom)), 0, courseItem.length, 0)
            courseItem.append("  ${startTimeArray[it.start - 1]}-${endTimeArray[it.start + it.step - 2]} at ${it.room}")
            style.addLine(courseItem)
        }
        style.addLine("具体详情请点击查看")
        builder.setStyle(style)
        notify(notificationManager, Constants.NOTIFICATION_ID_TOMORROW_COURSE, builder.build())
    }

    fun notifyTest(context: Context, notificationManager: NotificationManager, testList: List<Test>, colorArray: IntArray) {
        if (testList.isEmpty()) {
            cancel(notificationManager, Constants.NOTIFICATION_ID_TOMORROW_TEST)
            return
        }
        val title = "您明天有${testList.size}门考试，记得带上学生证和文具哦~"
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_TOMORROW)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_stat_init)
                .setContentTitle(title)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, QueryTestActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
        val style = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
        testList.forEachIndexed { index, it ->
            val courseItem = SpannableStringBuilder()
            courseItem.append(it.name)
            courseItem.setSpan(ForegroundColorSpan(colorArray[index]), 0, courseItem.length, 0)
            courseItem.append(" 时间：${it.time} 地点：${it.location}")
            style.addLine(courseItem)
        }
        style.addLine("具体详情请点击查看")
        builder.setStyle(style)
        notify(notificationManager, Constants.NOTIFICATION_ID_TOMORROW_TEST, builder.build())
    }

    private fun notify(notificationManager: NotificationManager, id: Int, notification: Notification) {
        notificationManager.notify(NOTIFICATION_TAG, id, notification)
    }

    fun cancel(notificationManager: NotificationManager, id: Int) {
        notificationManager.cancel(NOTIFICATION_TAG, id)
    }
}