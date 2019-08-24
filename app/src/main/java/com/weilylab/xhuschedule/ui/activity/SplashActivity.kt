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

package com.weilylab.xhuschedule.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.IntentConstant
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.SplashRepository
import com.weilylab.xhuschedule.service.CheckUpdateService
import com.weilylab.xhuschedule.service.DownloadSplashIntentService
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.FileUtil
import com.weilylab.xhuschedule.viewmodel.SplashViewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.PackageDataObserver
import vip.mystery0.tools.utils.FileTools
import vip.mystery0.tools.utils.sha1

/**
 * Created by mystery0.
 */
class SplashActivity : XhuBaseActivity(null, false) {
	private val splashViewModel: SplashViewModel by lazy { ViewModelProvider(this)[SplashViewModel::class.java] }

	private val splashObserver = object : PackageDataObserver<Splash> {
		override fun empty(data: Splash?) {
			gotoMain()
		}

		override fun error(data: Splash?, e: Throwable?) {
			Logs.wtf("error: ", e)
			gotoMain()
		}

		override fun content(data: Splash?) {
			todo(data!!)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		when (ConfigurationUtil.nightMode) {
			0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
			1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
		}
		super.onCreate(savedInstanceState)
	}

	override fun initView() {
		super.initView()
		if (ConfigurationUtil.firstEnter) {
			startActivity(Intent(this, GuideActivity::class.java))
			finish()
			return
		}
	}

	override fun initData() {
		super.initData()
		initViewModel()
		SplashRepository.requestSplash(splashViewModel)
		ContextCompat.startForegroundService(this, Intent(APP.context, CheckUpdateService::class.java))
		ConfigUtil.setTrigger(this)
		ConfigUtil.getCurrentYearAndTerm()
	}

	private fun initViewModel() {
		splashViewModel.splash.observe(this, splashObserver)
	}

	private fun todo(splash: Splash) {
		if (splash.enable) {
			val fileName = splash.splashUrl.sha1()
			val splashFile = FileUtil.getSplashImageFile(this, fileName)
			if (splashFile != null && splashFile.exists()) {
				val md5 = FileTools.instance.getMD5(splashFile)
				if (splash.imageMD5 == md5)
					gotoSplashImage()
				else {
					splashFile.delete()
					gotoMain()
				}
			} else {
				val intent = Intent(this, DownloadSplashIntentService::class.java)
				intent.putExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH, splash.splashUrl)
				intent.putExtra(IntentConstant.INTENT_TAG_NAME_SPLASH_FILE_NAME, fileName)
				startService(intent)
				gotoMain()
			}
		} else
			gotoMain()
	}

	private fun gotoMain() {
		startActivity(Intent(this, BottomNavigationActivity::class.java))
		finish()
	}

	private fun gotoSplashImage() {
		startActivity(Intent(this, SplashImageActivity::class.java))
		finish()
	}
}