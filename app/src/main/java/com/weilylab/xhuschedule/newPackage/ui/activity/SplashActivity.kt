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

package com.weilylab.xhuschedule.newPackage.ui.activity

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weilylab.xhuschedule.activity.GuideActivity
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.model.SplashResponse
import com.weilylab.xhuschedule.newPackage.repository.SplashRepository
import com.weilylab.xhuschedule.newPackage.utils.ConfigurationUtil
import com.weilylab.xhuschedule.newPackage.viewModel.SplashViewModel
import com.weilylab.xhuschedule.service.DownloadSplashIntentService
import com.weilylab.xhuschedule.util.*

/**
 * Created by mystery0.
 */
class SplashActivity : XhuBaseActivity(null) {
	private lateinit var splashViewModel: SplashViewModel

	private val splashObserver = Observer<SplashResponse.Splash> {
		if (it.isEnable) {
			Settings.splashTime = it.splashTime
			Settings.splashLocationUrl = it.locationUrl
			val intent = Intent(this@SplashActivity, DownloadSplashIntentService::class.java)
			intent.putExtra(Constants.INTENT_TAG_NAME_QINIU_PATH, it.splashUrl)
			intent.putExtra(Constants.INTENT_TAG_NAME_SPLASH_FILE_NAME, it.objectId)
			startService(intent)
			gotoSplashImage()
		} else
			gotoMain()
	}

	private val requestResultObserver = Observer<Int> {
		if (it == SplashRepository.ERROR)
			gotoMain()
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
//		ScheduleHelper.setTrigger(this)
//		ScheduleHelper.checkScreenWidth(this)
//		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
//			ScheduleHelper.scheduleJob(this)
		SplashRepository.requestSplash(splashViewModel)
	}

	private fun initViewModel() {
		splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
		splashViewModel.splash.observe(this, splashObserver)
		splashViewModel.requestResult.observe(this, requestResultObserver)
	}

	private fun gotoMain() {
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}

	private fun gotoSplashImage() {
		startActivity(Intent(this, SplashImageActivity::class.java))
		finish()
	}
}