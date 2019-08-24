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
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.repository.SplashRepository
import com.weilylab.xhuschedule.utils.FileUtil
import kotlinx.android.synthetic.main.activity_splash_image.*
import vip.mystery0.tools.utils.DensityTools
import vip.mystery0.tools.utils.sha1
import java.io.File

class SplashImageActivity : XhuBaseActivity(R.layout.activity_splash_image, false) {
	private val splash: Splash by lazy { SplashRepository.getSplash() }
	private var splashFile: File? = null

	override fun inflateView(layoutId: Int) {
		window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_FULLSCREEN
				or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
		super.inflateView(layoutId)
	}

	override fun initView() {
		super.initView()
		if (!splash.enable) {
			gotoMain()
			return
		}
		splashFile = FileUtil.getSplashImageFile(this, splash.splashUrl.sha1())
		if (splashFile == null || !splashFile!!.exists()) {
			gotoMain()
			return
		}
		Glide.with(this)
				.asBitmap()
				.load(splashFile)
				.apply(RequestOptions()
						.override(DensityTools.instance.getScreenWidth(), DensityTools.instance.getScreenHeight())
						.diskCacheStrategy(DiskCacheStrategy.NONE))
				.into(imageView)
		skipView.setTotalTime(splash.splashTime)
				.setUpdateTime(50)
				.setText(R.string.action_bypass)
				.setFinishAction {
					gotoMain()
				}
				.start()
	}

	override fun monitor() {
		super.monitor()
		imageView.setOnClickListener {
			if (splash.locationUrl != "")
				startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(splash.locationUrl)))
		}
		skipView.setOnClickListener {
			gotoMain()
		}
	}

	private fun gotoMain() {
		startActivity(Intent(this, BottomNavigationActivity::class.java))
		finish()
	}
}
