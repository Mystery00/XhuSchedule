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

package com.weilylab.xhuschedule.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.response.SplashResponse
import com.weilylab.xhuschedule.repository.SplashRepository
import com.weilylab.xhuschedule.utils.FileUtil
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
		splash = SplashRepository.getSplash()
		Glide.with(this)
				.asBitmap()
				.load(splashFile)
				.apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
				.into(imageView)
		val timer = Timer()
		timer.schedule(object : TimerTask() {
			override fun run() {
				gotoMain()
			}
		}, splash.splashTime)
		imageView.setOnClickListener {
			if (splash.locationUrl != "")
				startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(splash.locationUrl)))
		}
		button.setOnClickListener {
			timer.cancel()
			gotoMain()
		}
	}

	private fun gotoMain() {
		startActivity(Intent(this, BottomNavigationActivity::class.java))
		finish()
	}
}
