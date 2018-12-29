package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import vip.mystery0.logs.Logs

class WidgetUpdateService : Service() {
	override fun onBind(intent: Intent): IBinder? = null

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_init)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(Constants.NOTIFICATION_ID_WIDGET_UPDATE, notification)
		sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST))
		stopSelf()
	}

	override fun onDestroy() {
		stopForeground(true)
		super.onDestroy()
	}
}
