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

package com.weilylab.xhuschedule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.multidex.MultiDexApplication
import com.avos.avoscloud.*
import com.oasisfeng.condom.CondomContext
import com.tencent.tauth.Tencent
import com.weilylab.xhuschedule.activity.NoticeActivity
import com.weilylab.xhuschedule.listener.EmptyTencentListener
import com.weilylab.xhuschedule.util.Constants
import vip.mystery0.logs.Logs

/**
 * Created by myste.
 */
class APP : MultiDexApplication() {
	companion object {
		private var app: APP? = null
		@SuppressLint("StaticFieldLeak")
		lateinit var tencent: Tencent
		val tencentListener = EmptyTencentListener()

		fun getContext(): Context = app!!
	}

	init {
		app = this
	}

	override fun onCreate() {
		super.onCreate()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			PushService.setDefaultChannelId(applicationContext, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
		}
		AVOSCloud.initialize(applicationContext, Constants.LEANCLOUD_APP_ID, Constants.LEANCLOUD_APP_KEY)
		AVInstallation.getCurrentInstallation().saveInBackground()
		AVAnalytics.enableCrashReport(applicationContext, false)
//		AVOSCloud.setDebugLogEnabled(true)
		PushService.setDefaultPushCallback(applicationContext, NoticeActivity::class.java)
		tencent = Tencent.createInstance(Constants.QQ_API_KEY, CondomContext.wrap(applicationContext, "Tencent"))
		Logs.setLevel(Logs.Level.DEBUG)
	}
}