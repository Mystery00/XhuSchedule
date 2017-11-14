package com.weilylab.xhuschedule.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat

import com.weilylab.xhuschedule.R
import android.app.NotificationChannel
import android.graphics.Color
import android.os.Build
import com.weilylab.xhuschedule.classes.Version
import vip.mystery0.tools.fileUtil.FileUtil


object UpdateNotification
{
	private val NOTIFICATION_TAG = "Update"

	private fun initChannelID(context: Context)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			val id = "Xhu Schedule"
			val name = "WeiLy Studio"
			val description = "微力实验室"
			val importance = NotificationManager.IMPORTANCE_HIGH
			val mChannel = NotificationChannel(id, name, importance)
			mChannel.description = description
			mChannel.enableLights(true)
			mChannel.lightColor = Color.RED
			mNotificationManager.createNotificationChannel(mChannel)
		}
	}

	fun notify(context: Context, version: Version)
	{
		initChannelID(context)
		val res = context.resources
		val title = res.getString(R.string.update_notification_title, context.getString(R.string.app_version_name), version.versionName)
		val content = res.getString(R.string.update_notification_content, FileUtil.FormatFileSize(version.apkSize), FileUtil.FormatFileSize(version.patchSize))
		val bigText = res.getString(R.string.update_notification_big_text, version.updateLog)

		val builder = NotificationCompat.Builder(context, "Xhu Schedule")
				.setSmallIcon(R.drawable.ic_stat_update)
				.setContentTitle(title)
				.setContentText(content)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setStyle(NotificationCompat.BigTextStyle()
						.bigText(bigText))
				.setAutoCancel(true)

		notify(context, builder.build())
	}

	private fun notify(context: Context, notification: Notification)
	{
		val nm = context
				.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.notify(NOTIFICATION_TAG, 0, notification)
	}

	fun cancel(context: Context)
	{
		val nm = context
				.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.cancel(NOTIFICATION_TAG, 0)
	}
}
