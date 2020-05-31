/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.weilylab.xhuschedule.constant.Constants

object NotificationUtil {
	fun initChannelID(context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(createDefaultChannel())
			notificationManager.createNotificationChannel(createChannel(Constants.NOTIFICATION_CHANNEL_ID_DOWNLOAD, Constants.NOTIFICATION_CHANNEL_NAME_DOWNLOAD, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_DOWNLOAD, NotificationManager.IMPORTANCE_LOW))
			notificationManager.createNotificationChannel(createChannel(Constants.NOTIFICATION_CHANNEL_ID_TOMORROW, Constants.NOTIFICATION_CHANNEL_NAME_TOMORROW, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_TOMORROW, NotificationManager.IMPORTANCE_HIGH))
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private fun createDefaultChannel(): NotificationChannel {
		return createChannel(Constants.NOTIFICATION_CHANNEL_ID_DEFAULT, Constants.NOTIFICATION_CHANNEL_NAME_DEFAULT, null, NotificationManager.IMPORTANCE_LOW)
	}

	@TargetApi(Build.VERSION_CODES.O)
	private fun createChannel(channelID: String, channelName: String, channelDescription: String?, importance: Int): NotificationChannel {
		val channel = NotificationChannel(channelID, channelName, importance)
		channel.enableLights(true)
		channel.description = channelDescription
		channel.lightColor = Color.GREEN
		return channel
	}
}