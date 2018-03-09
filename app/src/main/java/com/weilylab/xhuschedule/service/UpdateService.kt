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

import android.app.IntentService
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Version
import com.weilylab.xhuschedule.interfaces.PhpService
import com.weilylab.xhuschedule.util.APPActivityManager
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import vip.mystery0.tools.fileUtil.FileUtil
import java.io.InputStreamReader

class UpdateService : IntentService("PhpService") {
	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(0, notification)
	}

	override fun onHandleIntent(intent: Intent?) {
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
							val title = getString(R.string.dialog_update_title, getString(R.string.app_version_name), version.versionName)
							val text = getString(R.string.dialog_update_text, version.updateLog)
							val currentActivity = APPActivityManager.appManager.currentActivity()
							if (currentActivity == null) {
								stopSelf()
								return
							}
							val builder = AlertDialog.Builder(currentActivity)
									.setTitle(title)
									.setMessage(text)
									.setPositiveButton("${getString(R.string.action_download_apk)}(${FileUtil.formatFileSize(version.apkSize)})", { _, _ ->
										val downloadAPKIntent = Intent(this@UpdateService, DownloadService::class.java)
										downloadAPKIntent.putExtra(Constants.INTENT_TAG_NAME_TYPE, Constants.DOWNLOAD_TYPE_APK)
										downloadAPKIntent.putExtra(Constants.INTENT_TAG_NAME_QINIU_PATH, version.apkQiniuPath)
										startService(downloadAPKIntent)
									})
							if (version.lastVersionCode == getString(R.string.app_version_code).toInt())
								builder.setNegativeButton("${getString(R.string.action_download_patch)}(${FileUtil.formatFileSize(version.patchSize)})", { _, _ ->
									val downloadPatchIntent = Intent(this@UpdateService, DownloadService::class.java)
									downloadPatchIntent.putExtra(Constants.INTENT_TAG_NAME_TYPE, Constants.DOWNLOAD_TYPE_PATCH)
									downloadPatchIntent.putExtra(Constants.INTENT_TAG_NAME_QINIU_PATH, version.patchQiniuPath)
									startService(downloadPatchIntent)
								})
							if (version.must)
								builder.setOnCancelListener {
									APPActivityManager.appManager.finishAllActivity()
								}
							else
								builder.setNeutralButton(R.string.action_download_cancel, { _, _ ->
									Settings.ignoreUpdate = version.versionCode
								})
							builder.show()
						}
						stopSelf()
					}

					override fun onNext(version: Version) {
						this.version = version
					}

					override fun onError(e: Throwable) {
						e.printStackTrace()
					}
				})
	}
}
