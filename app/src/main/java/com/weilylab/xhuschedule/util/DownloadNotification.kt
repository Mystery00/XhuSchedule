package com.weilylab.xhuschedule.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.download.Download
import vip.mystery0.tools.fileUtil.FileUtil


object DownloadNotification
{
	private val NOTIFICATION_TAG = "Download"
	private val NOTIFICATION_ID = 1
	private lateinit var notificationBuilder: NotificationCompat.Builder

	fun notify(context: Context)
	{
		notificationBuilder = NotificationCompat.Builder(context, "Xhu Schedule")
				.setSmallIcon(R.drawable.ic_stat_update)
				.setContentTitle("正在下载")
				.setContentText("下载中")
				.setAutoCancel(true)
		notify(context, notificationBuilder.build())
	}

	fun updateProgress(context: Context, download: Download)
	{
		notificationBuilder.setProgress(100, download.progress, false)
		notificationBuilder.setContentText(FileUtil.FormatFileSize(download.currentFileSize) + "/" + FileUtil.FormatFileSize(download.totalFileSize))
		notify(context, notificationBuilder.build())
	}

	fun downloadDone(context: Context)
	{
		val download = Download()
		download.progress = 100
		notificationBuilder.setProgress(0, 0, false)
		notificationBuilder.setContentText("File Downloaded")
		notify(context, notificationBuilder.build())
	}

	private fun notify(context: Context, notification: Notification)
	{
		val notificationManager = context
				.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification)
	}

	fun cancel(context: Context)
	{
		val notificationManager = context
				.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID)
	}
}
