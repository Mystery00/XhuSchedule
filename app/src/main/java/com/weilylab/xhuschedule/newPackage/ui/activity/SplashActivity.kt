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
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.config.APP
import com.weilylab.xhuschedule.newPackage.config.Status
import com.weilylab.xhuschedule.newPackage.constant.IntentConstant
import com.weilylab.xhuschedule.newPackage.model.response.SplashResponse
import com.weilylab.xhuschedule.newPackage.repository.SplashRepository
import com.weilylab.xhuschedule.newPackage.service.CheckUpdateService
import com.weilylab.xhuschedule.newPackage.utils.ConfigurationUtil
import com.weilylab.xhuschedule.newPackage.utils.FileUtil
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.PackageData
import com.weilylab.xhuschedule.newPackage.viewModel.SplashViewModel
import com.weilylab.xhuschedule.newPackage.service.DownloadSplashIntentService
import vip.mystery0.logs.Logs

/**
 * Created by mystery0.
 */
class SplashActivity : XhuBaseActivity(null) {
	private lateinit var splashViewModel: SplashViewModel

	private val splashObserver = Observer<PackageData<SplashResponse.Splash>> {
		when (it.status) {
			Status.Empty -> gotoMain()
			Status.Error -> {
				Logs.e("splashObserver", it.error)
				gotoMain()
			}
			Status.Content -> {
				todo(it.data!!)
			}
		}
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
		if (ConfigurationUtil.autoCheckUpdate)
			startService(Intent(APP.context, CheckUpdateService::class.java))
	}

	private fun initViewModel() {
		splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
		splashViewModel.splash.observe(this, splashObserver)
	}

	private fun todo(splash: SplashResponse.Splash) {
		if (splash.isEnable) {
			val splashFile = FileUtil.getSplashImageFile(this, splash.objectId)
			if (splashFile != null && splashFile.exists())
				gotoSplashImage()
			else {
				val intent = Intent(this, DownloadSplashIntentService::class.java)
				intent.putExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH, splash.splashUrl)
				intent.putExtra(IntentConstant.INTENT_TAG_NAME_SPLASH_FILE_NAME, splash.objectId)
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