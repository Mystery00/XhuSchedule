/*
 * Created by Mystery0 on 18-2-27 下午5:26.
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
 * Last modified 18-2-27 上午1:57
 */

package com.weilylab.xhuschedule.util.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Exam
import com.weilylab.xhuschedule.util.Constants

object TomorrowExamNotification {
    private const val NOTIFICATION_TAG = "TomorrowExam"

    fun notify(context: Context, id: Int, examList: ArrayList<Exam>) {
        val title = context.getString(R.string.tomorrow_exam_notification_title, examList.size)
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_TOMORROW)
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
        val style = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
        examList.forEach {
            val courseItem = SpannableStringBuilder()
            courseItem.append(it.name)
            courseItem.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, courseItem.length, 0)
            courseItem.append("\t考试时间：${it.time} 考试地点：${it.location}")
            style.addLine(courseItem)
        }
        builder.setStyle(style)

        notify(context, id, builder.build())
    }

    private fun notify(context: Context, id: Int, notification: Notification) {
        val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_TAG, id, notification)
    }

    fun cancel(context: Context, id: Int) {
        val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_TAG, id)
    }
}
