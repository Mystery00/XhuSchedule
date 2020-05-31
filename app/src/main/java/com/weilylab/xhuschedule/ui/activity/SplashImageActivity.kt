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
import coil.api.load
import coil.request.CachePolicy
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBaseActivity
import com.weilylab.xhuschedule.model.Splash
import com.weilylab.xhuschedule.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.activity_splash_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver
import vip.mystery0.tools.utils.screenHeight
import vip.mystery0.tools.utils.screenWidth
import java.io.File

class SplashImageActivity : XhuBaseActivity(R.layout.activity_splash_image, false) {
	private val splashViewModel: SplashViewModel by viewModel()

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
		skipView.setUpdateTime(50)
				.setText(R.string.action_bypass)
				.setFinishAction {
					gotoMain()
				}
	}

	override fun initData() {
		super.initData()
		splashViewModel.splashFile.observe(this, object : DataObserver<Pair<Splash, File>> {
			override fun empty() {
				super.empty()
				gotoMain()
			}

			override fun error( e: Throwable?) {
				super.error(e)
				Logs.wm(e)
				empty()
			}

			override fun contentNoEmpty(data: Pair<Splash, File>) {
				imageView.load(data.second) {
					size(screenWidth, screenHeight)
					diskCachePolicy(CachePolicy.DISABLED)
				}
				imageView.setOnClickListener {
					if (data.first.locationUrl != "")
						startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.first.locationUrl)))
				}
				skipView.setTotalTime(data.first.splashTime)
						.start()
			}
		})
		splashViewModel.getSplash()
	}

	override fun monitor() {
		super.monitor()
		skipView.setOnClickListener {
			gotoMain()
		}
	}

	private fun gotoMain() {
		startActivity(Intent(this, BottomNavigationActivity::class.java))
		finish()
	}
}
