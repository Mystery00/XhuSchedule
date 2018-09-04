package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.weilylab.xhuschedule.service.widget.WidgetUpdateService
import java.util.*

class WidgetService : Service() {
	override fun onBind(intent: Intent): IBinder? = null

	private lateinit var timer: Timer
	private lateinit var timerTask: TimerTask

	override fun onCreate() {
		super.onCreate()
		timer = Timer()
		timerTask = object : TimerTask() {
			override fun run() {
				val intent = Intent(this@WidgetService, WidgetUpdateService::class.java)
				ContextCompat.startForegroundService(this@WidgetService, intent)
			}
		}
		timer.schedule(timerTask, 1000, 600000)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		return START_STICKY
	}

	override fun onDestroy() {
		timerTask.cancel()
		timer.cancel()
		super.onDestroy()
	}
}
