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

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.metrics.AddTrace
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.service.UpdateService
import com.weilylab.xhuschedule.util.*
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.logs.Logs
import java.util.*

/**
 * Created by mystery0.
 */
class SplashActivity : XhuBaseActivity() {
	private lateinit var initDialog: Dialog

	override fun initView() {
		super.initView()
		initDialog = ZLoadingDialog(this)
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText(getString(R.string.hint_dialog_do_update))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setCancelable(false)
				.setLoadingColor(ContextCompat.getColor(this, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent))
				.create()
	}

	override fun initData() {
		super.initData()
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.START_DATE, Calendar.getInstance().time.toString())
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params)
		ScheduleHelper.initChannelID(APP.getContext())//初始化NotificationChannelID
		ScheduleHelper.setTrigger(this)
		ScheduleHelper.checkScreenWidth(this)
		if (Settings.autoCheckUpdate)
			startService(Intent(this, UpdateService::class.java))
		if (Settings.isFirstRun210)
			Observable.create<Any> {
				//初始化主用户
				val userList = XhuFileUtil.getArrayListFromFile(XhuFileUtil.getStudentListFile(this@SplashActivity), Student::class.java)
				var hasMain = false
				userList.forEach {
					if (it.isMain && hasMain)
						it.isMain = false
					hasMain = hasMain || it.isMain
				}
				if (!hasMain && userList.size > 1)
					userList[0].isMain = true
				//初始化颜色
				XhuFileUtil.removeAllSavedPreference(this, Constants.SHARED_PREFERENCE_COURSE_COLOR)
				it.onComplete()
			}
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Observer<Any> {
						override fun onComplete() {
							initDialog.dismiss()
							go()
						}

						override fun onSubscribe(d: Disposable) {
							initDialog.show()
						}

						override fun onNext(t: Any) {
						}

						override fun onError(e: Throwable) {
							Logs.wtf(TAG, "onError: ", e)
							initDialog.dismiss()
							go()
						}
					})
		else
			go()
	}

	private fun go() {
		if (Settings.isFirstEnter)
			startActivity(Intent(this, WelcomeActivity::class.java))
		else
			startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}