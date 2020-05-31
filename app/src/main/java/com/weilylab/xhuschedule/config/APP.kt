/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.config

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.oasisfeng.condom.CondomContext
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.auth.AuthInfo
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.Tencent
import com.weilylab.xhuschedule.BuildConfig
import com.weilylab.xhuschedule.module.*
import com.weilylab.xhuschedule.utils.NotificationUtil
import com.weilylab.xhuschedule.utils.PackageUtil
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import vip.mystery0.crashhandler.CrashHandler
import vip.mystery0.logs.Logs
import vip.mystery0.logs.logsLogger
import vip.mystery0.tools.ToolsClient
import vip.mystery0.tools.utils.registerActivityLifecycle
import java.io.File

/**
 * Created by myste.
 */
class APP : MultiDexApplication() {
	override fun onCreate() {
		super.onCreate()
		context = applicationContext
		instance = this
		startKoin {
			logsLogger(Level.ERROR)
			androidContext(this@APP)
			modules(listOf(appModule, databaseModule, networkModule, repositoryModule, viewModelModule))
		}
		CrashHandler.config {
			setFileNameSuffix("log")
					.setDir(File(externalCacheDir, "crash"))
					.setAutoClean(true)
					.setDebug(BuildConfig.DEBUG)
		}.init()
		NotificationUtil.initChannelID(this)//初始化NotificationChannelID
		ToolsClient.initWithContext(this)
		registerActivityLifecycle()
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