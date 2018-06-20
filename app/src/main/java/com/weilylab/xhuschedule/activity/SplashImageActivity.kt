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

package com.weilylab.xhuschedule.activity

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil
import kotlinx.android.synthetic.main.activity_splash_image.*
import java.util.*

class SplashImageActivity : XhuBaseActivity(R.layout.activity_splash_image) {
	override fun initView() {
		super.initView()
		val objectId = intent.getStringExtra(Constants.INTENT_TAG_NAME_SPLASH_FILE_NAME)
		if (TextUtils.isEmpty(objectId)) {
			go()
			return
		}
		val splashFile = XhuFileUtil.getSplashImageFile(this, objectId)
		Glide.with(this)
				.asBitmap()
				.load(splashFile)
				.into(imageView)
		val timer = Timer()
		timer.schedule(object : TimerTask() {
			override fun run() {
				go()
			}
		}, Settings.splashTime)
		imageView.setOnClickListener {
			if (Settings.splashLocationUrl != "")
				startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Settings.splashLocationUrl)))
		}
		button.setOnClickListener {
			go()
			timer.cancel()
		}
	}

	private fun go() {
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}
