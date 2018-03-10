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

package com.weilylab.xhuschedule.activity

import android.content.Intent
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.service.UpdateService
import com.weilylab.xhuschedule.util.*
import java.util.*

/**
 * Created by mystery0.
 */
class SplashActivity : XhuBaseActivity() {

	override fun initData() {
		super.initData()
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.START_DATE, Calendar.getInstance().time.toString())
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params)
		ScheduleHelper.initChannelID(APP.getContext())//初始化NotificationChannelID
		ScheduleHelper.setTrigger(this)
		ScheduleHelper.checkScreenWidth(this)
		if (Settings.autoCheckUpdate)
			startService(Intent(this, UpdateService::class.java))
		go()
	}

	private fun go() {
		if (Settings.isFirstEnter)
			startActivity(Intent(this, WelcomeActivity::class.java))
		else
			startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}