/*
 * Created by Mystery0 on 18-2-21 下午9:12.
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
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.widget.WidgetHelper

class WidgetLastActionService : Service() {
	private var isRun = false

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_foreground)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.build()
		startForeground(Constants.NOTIFICATION_ID_WIDGET_ACTION_LAST, notification)
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		if (isRun)
			return super.onStartCommand(intent, flags, startId)
		Thread(Runnable {
			val tag = intent.getStringExtra(Constants.INTENT_TAG_NAME_TAG)
			when (tag) {
				WidgetHelper.TODAY_TAG -> {
					if (WidgetHelper.dayIndex > 1)
						WidgetHelper.dayIndex--
					WidgetHelper.refreshTodayCourses(this)
					sendBroadcast(Intent(Constants.ACTION_WIDGET_UPDATE_BROADCAST)
							.putExtra(Constants.INTENT_TAG_NAME_TAG, tag))
				}
			}
			stopSelf()
		}).start()
		return super.onStartCommand(intent, flags, startId)
	}
}
