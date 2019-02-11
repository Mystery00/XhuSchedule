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

package com.weilylab.xhuschedule.config

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDexApplication
import com.jinrishici.sdk.android.factory.JinrishiciFactory
import com.oasisfeng.condom.CondomContext
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.auth.AuthInfo
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.Tencent
import com.weilylab.xhuschedule.BuildConfig
import com.weilylab.xhuschedule.repository.local.db.DBHelper
import com.weilylab.xhuschedule.utils.NotificationUtil
import com.weilylab.xhuschedule.utils.PackageUtil
import vip.mystery0.crashhandler.CrashHandler
import vip.mystery0.logs.Logs
import vip.mystery0.tools.ToolsClient

/**
 * Created by myste.
 */
class APP : MultiDexApplication() {
	override fun onCreate() {
		super.onCreate()
		context = applicationContext
		instance = this
		CrashHandler.config {
			it.setFileNameSuffix("log")
					.setDir(externalCacheDir!!)
					.setDirName("crash")
					.setAutoClean(true)
					.setDebug(BuildConfig.DEBUG)
		}.initWithContext(this)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !BuildConfig.DEBUG)//大于指定的sdk再启用日志上传
			try {
				CrashReport.initCrashReport(CondomContext.wrap(applicationContext, "Bugly"), "7fe1820ab7", BuildConfig.DEBUG)
			} catch (ignore: Exception) {
				CrashReport.initCrashReport(applicationContext, "7fe1820ab7", BuildConfig.DEBUG)
			}
		DBHelper.init(this)
		NotificationUtil.initChannelID(APP.context)//初始化NotificationChannelID
		if (PackageUtil.isQQApplicationAvailable())
			tencent = try {
				Tencent.createInstance("1106663023", CondomContext.wrap(applicationContext, "Tencent"))
			} catch (ignore: Exception) {
				Tencent.createInstance("1106663023", applicationContext)
			}
		if (PackageUtil.isWeiXinApplicationAvailable())
			wxAPI = try {
				WXAPIFactory.createWXAPI(CondomContext.wrap(applicationContext, "WeiXin"), "wx41799887957cbba8", false)
			} catch (ignore: Exception) {
				WXAPIFactory.createWXAPI(applicationContext, "wx41799887957cbba8", false)
			}
		if (PackageUtil.isWeiBoApplicationAvailable())
			try {
				WbSdk.install(CondomContext.wrap(applicationContext, "WeiBo"), AuthInfo(CondomContext.wrap(applicationContext, "WeiBo"), "2170085314", "https://api.weibo.com/oauth2/default.html", "statuses/share"))
			} catch (ignore: Exception) {
				WbSdk.install(CondomContext.wrap(applicationContext, "WeiBo"), AuthInfo(applicationContext, "2170085314", "https://api.weibo.com/oauth2/default.html", "statuses/share"))
			}
		Logs.setConfig {
			it.setShowLog(BuildConfig.DEBUG)
		}
		ToolsClient.initWithContext(this)
		JinrishiciFactory.init(this)
	}

	companion object {
		@SuppressLint("StaticFieldLeak")
		lateinit var context: Context
			private set

		lateinit var instance: Application
			private set

		var tencent: Tencent? = null
			private set

		var wxAPI: IWXAPI? = null
			private set
	}
}