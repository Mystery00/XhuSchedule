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
import android.support.multidex.MultiDexApplication
import com.oasisfeng.condom.CondomContext
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.tauth.Tencent
import com.weilylab.xhuschedule.listener.EmptyTencentListener
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.SkinLoader
import skin.support.SkinCompatManager
import skin.support.app.SkinCardViewInflater
import skin.support.constraint.app.SkinConstraintViewInflater
import skin.support.design.app.SkinMaterialViewInflater
import vip.mystery0.tools.logs.Logs

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
//		CrashReport.initCrashReport(CondomContext.wrap(applicationContext, "Bugly"), Constants.BUGLY_API_KEY, true)
		tencent = Tencent.createInstance(Constants.QQ_API_KEY, CondomContext.wrap(applicationContext, "Tencent"))
		Logs.setLevel(Logs.Debug)
		SkinCompatManager.withoutActivity(this)
				.addStrategy(SkinLoader())
				.addInflater(SkinMaterialViewInflater())
				.addInflater(SkinConstraintViewInflater())
				.addInflater(SkinCardViewInflater())
				.loadSkin()
	}
}