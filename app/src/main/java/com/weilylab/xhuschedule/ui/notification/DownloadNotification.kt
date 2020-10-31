/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Download
import vip.mystery0.tools.utils.toFormatFileSize

object DownloadNotification {
    private const val NOTIFICATION_TAG = "DownloadNotification"
    private var NOTIFICATION_ID = Constants.NOTIFICATION_ID_DOWNLOAD

    @SuppressLint("StaticFieldLeak")
    private lateinit var notificationBuilder: NotificationCompat.Builder

    fun notify(context: Context, fileName: String) {
        notificationBuilder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_DOWNLOAD)
                .setSound(null)
                .setVibrate(null)
                .setSmallIcon(R.drawable.ic_file_download_white_24dp)
                .setContentTitle(context.getString(R.string.download_notification_title, fileName))
                .setOngoing(true)
                .setAutoCancel(true)
        notify(context, notificationBuilder.build())
    }

    fun updateProgress(context: Context, download: Download) {
        notificationBuilder.setProgress(100, download.progress, false)
                .setContentText(context.getString(R.string.download_notification_title_download, download.currentFileSize.toFormatFileSize(), download.totalFileSize.toFormatFileSize()))
                .setSubText(context.getString(R.string.download_notification_text, download.progress))
        notify(context, notificationBuilder.build())
    }

    fun downloadError(context: Context) {
        cancel(context)
        notificationBuilder.setProgress(0, 0, false)
                .setContentTitle(context.getString(R.string.error_download))
                .setContentText(" ")
                .setOngoing(false)
        notify(context, notificationBuilder.build())
    }

    fun downloadFileMD5Matching(context: Context) {
        cancel(context)
        notificationBuilder.setProgress(0, 0, false)
                .setContentTitle(context.getString(R.string.download_notification_md5_matching))
                .setContentText(" ")
                .setOngoing(false)
        notify(context, notificationBuilder.build())
    }

    fun downloadFileMD5NotMatch(context: Context) {
        cancel(context)
        notificationBuilder.setProgress(0, 0, false)
                .setContentTitle(context.getString(R.string.error_download_md5_not_matched))
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
        notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID++)
    }
}
