/*
 * Created by Mystery0 on 4/3/18 11:52 PM.
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
 * Last modified 4/3/18 11:52 PM
 */

package com.weilylab.xhuschedule.newPackage.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.MainActivity
import com.weilylab.xhuschedule.newPackage.base.XhuBaseActivity
import com.weilylab.xhuschedule.newPackage.model.SplashResponse
import com.weilylab.xhuschedule.newPackage.repository.SplashRepository
import com.weilylab.xhuschedule.newPackage.utils.FileUtil
import com.weilylab.xhuschedule.util.Settings
import kotlinx.android.synthetic.main.activity_splash_image.*
import java.io.File
import java.util.*

class SplashImageActivity : XhuBaseActivity(R.layout.activity_splash_image) {
	private lateinit var splash: SplashResponse.Splash
	private var splashFile: File? = null

	override fun inflateView(layoutId: Int) {
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
		super.inflateView(layoutId)
	}

	override fun initData() {
		super.initData()
		splash = SplashRepository.getSplash()
		if (!splash.isEnable) {
			gotoMain()
			return
		}
		splashFile = FileUtil.getSplashImageFile(this, splash.objectId)
		if (splashFile == null || !splashFile!!.exists()) {
			gotoMain()
			return
		}
	}

	override fun initView() {
		super.initView()
		Glide.with(this)
				.asBitmap()
				.load(splashFile)
				.into(imageView)
		val timer = Timer()
		timer.schedule(object : TimerTask() {
			override fun run() {
				gotoMain()
			}
		}, Settings.splashTime)
		imageView.setOnClickListener {
			if (Settings.splashLocationUrl != "")
				startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Settings.splashLocationUrl)))
		}
		button.setOnClickListener {
			gotoMain()
			timer.cancel()
		}
	}

	private fun gotoMain() {
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}
