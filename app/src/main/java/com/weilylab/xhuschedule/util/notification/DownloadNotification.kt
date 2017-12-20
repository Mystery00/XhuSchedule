/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 上午11:17
 */

package com.weilylab.xhuschedule.util.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.download.Download
import vip.mystery0.tools.fileUtil.FileUtil

object DownloadNotification {
    private val NOTIFICATION_TAG = "Download"
    private val NOTIFICATION_ID = 1
    private lateinit var notificationBuilder: NotificationCompat.Builder

    fun notify(context: Context, fileName: String) {
        UpdateNotification.cancel(context)
        notificationBuilder = NotificationCompat.Builder(context, "Xhu Schedule")
                .setSound(null)
                .setVibrate(null)
                .setSmallIcon(R.drawable.ic_stat_update)
                .setContentTitle(context.getString(R.string.download_notification_title, fileName))
                .setOngoing(true)
                .setAutoCancel(true)
        notify(context, notificationBuilder.build())
    }

    fun updateProgress(context: Context, download: Download) {
        notificationBuilder.setProgress(100, download.progress, false)
                .setContentText(context.getString(R.string.download_notification_title_download, FileUtil.FormatFileSize(download.currentFileSize), FileUtil.FormatFileSize(download.totalFileSize)))
                .setSubText(context.getString(R.string.download_notification_text, download.progress))
        notify(context, notificationBuilder.build())
    }

    fun downloadError(context: Context) {
        notificationBuilder.setProgress(0, 0, false)
                .setContentTitle(context.getString(R.string.error_download))
                .setContentText(" ")
                .setOngoing(false)
        notify(context, notificationBuilder.build())
    }

    private fun notify(context: Context, notification: Notification) {
        val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID)
    }
}
