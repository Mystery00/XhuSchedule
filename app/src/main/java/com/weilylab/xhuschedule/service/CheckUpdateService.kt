package com.weilylab.xhuschedule.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.api.PhpAPI
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.Version
import com.weilylab.xhuschedule.utils.APPActivityManager
import com.weilylab.xhuschedule.utils.rxAndroid.RxObservable
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.ui.activity.GuideActivity
import com.weilylab.xhuschedule.ui.activity.SplashActivity
import com.weilylab.xhuschedule.ui.activity.SplashImageActivity
import com.weilylab.xhuschedule.ui.fragment.settings.SettingsPreferenceFragment
import com.weilylab.xhuschedule.utils.ConfigUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.FileTools

class CheckUpdateService : Service() {
	override fun onBind(intent: Intent): IBinder? = null

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_init)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(Constants.NOTIFICATION_ID_CHECK_UPDATE, notification)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val appVersion = "${getString(R.string.app_version_name)}-${getString(R.string.app_version_code)}"
		val systemVersion = "Android ${Build.VERSION.RELEASE}-${Build.VERSION.SDK_INT}"
		val manufacturer = Build.MANUFACTURER
		val model = Build.MODEL
		val rom = Build.DISPLAY
		RetrofitFactory.retrofit
				.create(PhpAPI::class.java)
				.checkVersion(appVersion, systemVersion, manufacturer, model, rom, ConfigUtil.getDeviceID(this))
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { GsonFactory.parse<Version>(it) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<Version>() {
					override fun onFinish(data: Version?) {
						if (data != null && data.versionCode.toInt() > getString(R.string.app_version_code).toInt())
							showUpdateDialog(data)
						stopSelf()
						LocalBroadcastManager.getInstance(this@CheckUpdateService).sendBroadcast(Intent(SettingsPreferenceFragment.ACTION_CHECK_UPDATE_DONE))
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						stopSelf()
					}
				})
		return super.onStartCommand(intent, flags, startId)
	}

	private fun showUpdateDialog(version: Version) {
		RxObservable<Boolean>()
				.doThings {
					while (APPActivityManager.currentActivity() is SplashActivity || APPActivityManager.currentActivity() is GuideActivity || APPActivityManager.currentActivity() is SplashImageActivity)
						Thread.sleep(1000)
					it.onFinish(true)
				}
				.subscribe(object : RxObserver<Boolean>() {
					override fun onFinish(data: Boolean?) {
						if (data != null && data) {
							val activity = APPActivityManager.currentActivity() ?: return
							val title = getString(R.string.dialog_update_title, getString(R.string.app_version_name), version.versionName)
							val text = getString(R.string.dialog_update_text, version.updateLog)
							val builder = AlertDialog.Builder(activity)
									.setTitle(title)
									.setMessage(text)
									.setPositiveButton("${getString(R.string.action_download_apk)}(${FileTools.formatFileSize(version.apkSize.toLong())})") { _, _ ->
										DownloadService.intentTo(activity, Constants.DOWNLOAD_TYPE_APK, version.apkQiniuPath, version.apkMD5, version.patchMD5)
									}
							if (version.lastVersionCode == getString(R.string.app_version_code))
								builder.setNegativeButton("${getString(R.string.action_download_patch)}(${FileTools.formatFileSize(version.patchSize.toLong())})") { _, _ ->
									DownloadService.intentTo(activity, Constants.DOWNLOAD_TYPE_PATCH, version.patchQiniuPath, version.apkMD5, version.patchMD5)
								}
							if (version.must == "1")
								builder.setOnCancelListener {
									APPActivityManager.finishAllActivity()
								}
							else
								builder.setNeutralButton(R.string.action_download_cancel, null)
							builder.show()
						}
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}
				})
	}
}
