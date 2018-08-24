package com.weilylab.xhuschedule.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Test
import com.zhuangfei.timetable.model.Schedule

object TomorrowNotification {
	private const val NOTIFICATION_TAG = "TomorrowNotification"

	fun notifyCourse(context: Context, courseList: List<Schedule>) {
		val title = ""
		val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_TOMORROW)
				.setDefaults(Notification.DEFAULT_ALL)
				.setSmallIcon(R.drawable.ic_stat_init)
				.setContentTitle(title)
				.setContentText("placeholder")
				.setContentIntent(PendingIntent.getActivity(context, 0, context.packageManager.getLaunchIntentForPackage(context.packageName), PendingIntent.FLAG_UPDATE_CURRENT))
				.setAutoCancel(true)
		val style = NotificationCompat.InboxStyle()
				.setBigContentTitle(title)
		val startTimeArray = context.resources.getStringArray(R.array.start_time)
		val endTimeArray = context.resources.getStringArray(R.array.end_time)
		courseList.forEach {
			val courseItem = SpannableStringBuilder()
			courseItem.append(it.name)
			courseItem.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, courseItem.length, 0)
			courseItem.append("\t${startTimeArray[it.start - 1]}-${endTimeArray[it.start + it.step - 2]} at ${it.room}")
			style.addLine(courseItem)
		}
		builder.setStyle(style)
		notify(context, Constants.NOTIFICATION_ID_TOMORROW_COURSE, builder.build())
	}

	fun notifyTest(context: Context, testList: List<Test>) {
		val title = ""
		val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_TOMORROW)
				.setDefaults(Notification.DEFAULT_ALL)
				.setSmallIcon(R.drawable.ic_stat_init)
				.setContentTitle(title)
				.setContentText("placeholder")
				.setContentIntent(PendingIntent.getActivity(context, 0, context.packageManager.getLaunchIntentForPackage(context.packageName), PendingIntent.FLAG_UPDATE_CURRENT))
				.setAutoCancel(true)
		val style = NotificationCompat.InboxStyle()
				.setBigContentTitle(title)
		testList.forEach {
			val courseItem = SpannableStringBuilder()
			courseItem.append(it.name)
			courseItem.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), 0, courseItem.length, 0)
			courseItem.append("\t考试时间：${it.time} 考试地点：${it.location}")
			style.addLine(courseItem)
		}
		builder.setStyle(style)
		notify(context, Constants.NOTIFICATION_ID_TOMORROW_TEST, builder.build())
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