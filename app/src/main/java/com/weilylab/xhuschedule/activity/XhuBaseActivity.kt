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

package com.weilylab.xhuschedule.activity

import android.support.v7.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.oasisfeng.condom.CondomContext
import com.oasisfeng.condom.CondomOptions
import com.weilylab.xhuschedule.util.APPActivityManager
import vip.mystery0.tools.base.BaseActivity

abstract class XhuBaseActivity : BaseActivity() {
	lateinit var mFirebaseAnalytics: FirebaseAnalytics

	override fun initView() {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
		val condom = CondomContext.wrap(this, "Firebase", CondomOptions().setOutboundJudge { _, _, target_package ->
			target_package == "com.google.android.gms"
		})
		FirebaseApp.initializeApp(condom)
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(condom)
		APPActivityManager.appManager.addActivity(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		APPActivityManager.appManager.finishActivity(this)
	}
}