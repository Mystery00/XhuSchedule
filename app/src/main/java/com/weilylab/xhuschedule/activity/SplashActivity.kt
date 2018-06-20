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
import android.os.Build
import android.support.v4.content.ContextCompat
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.service.DownloadSplashIntentService
import com.weilylab.xhuschedule.util.*
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs

/**
 * Created by mystery0.
 */
class SplashActivity : XhuBaseActivity(null) {

	override fun initData() {
		super.initData()
		ScheduleHelper.initChannelID(APP.getContext())//初始化NotificationChannelID
		ScheduleHelper.setTrigger(this)
		ScheduleHelper.checkScreenWidth(this)
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
			ScheduleHelper.scheduleJob(this)
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
				//请求启动页图片
//				if (ScheduleHelper.isConnectInternet(this)) {
//					try {
//						val avQuery = AVQuery<AVObject>(Constants.TABLE_NAME_SPLASH)
//						avQuery.orderByDescending("indexID")
//						avQuery.limit(1)
//						avQuery.findInBackground(object : FindCallback<AVObject>() {
//							override fun done(mutableList: MutableList<AVObject>?, avException: AVException?) {
//								if (avException == null) {
//									val avObject = mutableList!![0]
//									val isEnable = avObject.getBoolean("isEnable")
//									if (!isEnable) {
//										val preferenceList = arrayOf(Constants.SPLASH_IMAGE_FILE_NAME, Constants.SPLASH_LOCATION_URL, Constants.SPLASH_TIME)
//										XhuFileUtil.removeSavedPreference(this@SplashActivity, Constants.SHARED_PREFERENCE_SETTINGS, preferenceList)
//										return
//									}
//									val objectId = avObject.objectId
//									val splashUrl = avObject.getString("splashUrl")
//									val splashTime = avObject.getInt("splashTime")
//									val locationUrl = avObject.getString("locationUrl")
//									Logs.i(TAG, "done: $objectId")
//									Logs.i(TAG, "done: $splashUrl")
//									Logs.i(TAG, "done: $splashTime")
//									Logs.i(TAG, "done: $locationUrl")
//									Settings.splashTime = splashTime.toLong()
//									Settings.splashLocationUrl = locationUrl
//									val intent = Intent(this@SplashActivity, DownloadSplashIntentService::class.java)
//									intent.putExtra(Constants.INTENT_TAG_NAME_QINIU_PATH, splashUrl)
//									intent.putExtra(Constants.INTENT_TAG_NAME_SPLASH_FILE_NAME, objectId)
//									startService(intent)
//								} else {
//									Logs.wtf(TAG, "done: ", avException)
//								}
//							}
//						})
//					} catch (e: Exception) {
//						Logs.wtf(TAG, "initData: ", e)
//					}
//				}
				it.onComplete()
			}
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Observer<Any> {
						override fun onComplete() {
							go()
						}

						override fun onSubscribe(d: Disposable) {
						}

						override fun onNext(t: Any) {
						}

						override fun onError(e: Throwable) {
							Logs.wtf("onError: ", e)
							go()
						}
					})
		else
			go()
	}

	private fun go() {
		if (Settings.isFirstEnter)
			startActivity(Intent(this, GuideActivity::class.java))
		else {
			val splashImageFileName = Settings.splashImage
			val file = XhuFileUtil.getSplashImageFile(this, splashImageFileName)
			if (file == null || !file.exists()) {
				startActivity(Intent(this, MainActivity::class.java))
				finish()
				return
			}
			val intent = Intent(this, SplashImageActivity::class.java)
			intent.putExtra(Constants.INTENT_TAG_NAME_SPLASH_FILE_NAME, splashImageFileName)
			startActivity(intent)
		}
		finish()
	}
}