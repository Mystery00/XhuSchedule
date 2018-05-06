/*
 * Created by Mystery0 on 4/6/18 6:42 PM.
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
 * Last modified 4/6/18 6:42 PM
 */

package com.weilylab.xhuschedule.service

import android.app.ActivityManager
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import com.weilylab.xhuschedule.activity.NoticeActivity
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.widget.WidgetHelper
import vip.mystery0.logs.Logs


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CheckJobService : JobService() {
	private val TAG = "CheckJobService"

	override fun onCreate() {
		super.onCreate()
		Logs.i(TAG, "onCreate: ")
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Logs.i(TAG, "onStartCommand: ")
		ScheduleHelper.scheduleJob(this)
		return Service.START_NOT_STICKY
	}

	override fun onStopJob(params: JobParameters?): Boolean {
		Logs.i(TAG, "onStopJob: ")
		ScheduleHelper.scheduleJob(this)
		return true
	}

	override fun onStartJob(params: JobParameters?): Boolean {
		Logs.i(TAG, "onStartJob: ")
		if (WidgetHelper.getWidgetIds(this, WidgetHelper.TODAY_TAG).isNotEmpty() || WidgetHelper.getWidgetIds(this, WidgetHelper.TABLE_TAG).isNotEmpty() || WidgetHelper.getWidgetIds(this, WidgetHelper.EXAM_TAG).isNotEmpty())
			ContextCompat.startForegroundService(this, Intent(this, WidgetInitService::class.java))
		ScheduleHelper.setTrigger(this)
		return true
	}
}