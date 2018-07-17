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

package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Version
import com.weilylab.xhuschedule.interfaces.PhpService
import com.weilylab.xhuschedule.receiver.CheckUpdateReceiver
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.ScheduleHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import java.io.InputStreamReader

class UpdateService : Service() {
	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	private val TAG = "UpdateService"
	private lateinit var localBroadcastManager: LocalBroadcastManager
	private lateinit var checkUpdateReceiver: CheckUpdateReceiver

	override fun onCreate() {
		Logs.i(TAG, "onCreate: ")
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_foreground)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(0, notification)
		localBroadcastManager = LocalBroadcastManager.getInstance(this)
		val intentFilter = IntentFilter()
		intentFilter.addAction(Constants.ACTION_CHECK_UPDATE)
		checkUpdateReceiver = CheckUpdateReceiver()
		localBroadcastManager.registerReceiver(checkUpdateReceiver, intentFilter)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		ScheduleHelper.phpRetrofit
				.create(PhpService::class.java)
				.checkVersion()
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { responseBody -> Gson().fromJson(InputStreamReader(responseBody.byteStream()), Version::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : DisposableObserver<Version>() {
					private lateinit var version: Version
					override fun onComplete() {
						if (version.versionCode > getString(R.string.app_version_code).toInt()) {
							Logs.i(TAG, "onComplete: ${version.versionCode}")
							val versionIntent = Intent(Constants.ACTION_CHECK_UPDATE)
							val bundle = Bundle()
							bundle.putSerializable(Constants.INTENT_TAG_NAME_VERSION, version)
							versionIntent.putExtra(Constants.INTENT_TAG_NAME_VERSION, bundle)
							localBroadcastManager.sendBroadcast(versionIntent)
						}
						Thread(Runnable {
							Thread.sleep(10000)
							stopSelf()
						}).start()
					}

					override fun onNext(version: Version) {
						this.version = version
					}

					override fun onError(e: Throwable) {
						e.printStackTrace()
					}
				})
		return super.onStartCommand(intent, flags, startId)
	}

	override fun onDestroy() {
		Logs.i(TAG, "onDestroy: ")
		super.onDestroy()
		localBroadcastManager.unregisterReceiver(checkUpdateReceiver)
	}
}
