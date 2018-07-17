/*
 * Created by Mystery0 on 4/6/18 4:28 PM.
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
 * Last modified 4/6/18 4:28 PM
 */

package com.weilylab.xhuschedule.util.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.LeanCloudPush
import com.weilylab.xhuschedule.util.Constants

object PushMessageNotification {
	private const val NOTIFICATION_TAG = "PushMessage"

	fun notify(context: Context, leanCloudPush: LeanCloudPush) {
		val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_PUSH)
				.setSmallIcon(R.drawable.ic_stat_push_message)
				.setContentTitle(leanCloudPush.title)
				.setContentText(leanCloudPush.content)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setStyle(NotificationCompat.BigTextStyle()
						.bigText(leanCloudPush.content)
						.setBigContentTitle(leanCloudPush.title))
				.setAutoCancel(true)
		when (leanCloudPush.level) {
			LeanCloudPush.LEVEL_LOW -> builder.setSound(null)
					.setVibrate(null).priority = NotificationCompat.PRIORITY_LOW
			LeanCloudPush.LEVEL_DEFAULT -> builder.setDefaults(Notification.DEFAULT_ALL).priority = NotificationCompat.PRIORITY_DEFAULT
			LeanCloudPush.LEVEL_HIGH -> builder.setDefaults(Notification.DEFAULT_ALL).priority = NotificationCompat.PRIORITY_HIGH
		}
		if (leanCloudPush.link.startsWith("xhuschedule"))
			builder.setContentIntent(
					PendingIntent.getActivity(
							context,
							0,
							Intent(Constants.ACTION_NOTIFICATION_VIEW, Uri.parse(leanCloudPush.link)),
							PendingIntent.FLAG_UPDATE_CURRENT))
		else
			builder.setContentIntent(
					PendingIntent.getActivity(
							context,
							0,
							Intent(Intent.ACTION_VIEW, Uri.parse(leanCloudPush.link)),
							PendingIntent.FLAG_UPDATE_CURRENT))
		notify(context, builder.build())
	}

	private fun notify(context: Context, notification: Notification) {
		val notificationManager = context
				.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(NOTIFICATION_TAG, Constants.NOTIFICATION_ID_LEAN_CLOUD_PUSH, notification)
	}

	fun cancel(context: Context) {
		val notificationManager = context
				.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NOTIFICATION_TAG, Constants.NOTIFICATION_ID_LEAN_CLOUD_PUSH)
	}
}
