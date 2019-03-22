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
import com.weilylab.xhuschedule.api.XhuScheduleCloudAPI
import com.weilylab.xhuschedule.factory.GsonFactory
import com.weilylab.xhuschedule.factory.RetrofitFactory
import com.weilylab.xhuschedule.model.Version
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.ui.activity.GuideActivity
import com.weilylab.xhuschedule.ui.activity.SplashActivity
import com.weilylab.xhuschedule.ui.activity.SplashImageActivity
import com.weilylab.xhuschedule.ui.fragment.settings.SettingsPreferenceFragment
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.tools.utils.ActivityManagerTools
import vip.mystery0.tools.utils.FileTools

class CheckUpdateService : Service() {
	companion object {
		const val CHECK_ACTION_BY_MANUAL = "check_action_by_manual"
	}

	override fun onBind(intent: Intent): IBinder? = null

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.mipmap.ic_stat_init)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(Constants.NOTIFICATION_ID_CHECK_UPDATE, notification)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		if (intent == null)
			return super.onStartCommand(intent, flags, startId)
		val appVersion = "${getString(R.string.app_version_name)}-${getString(R.string.app_version_code)}"
		val systemVersion = "Android ${Build.VERSION.RELEASE}-${Build.VERSION.SDK_INT}"
		val manufacturer = Build.MANUFACTURER
		val model = Build.MODEL
		val rom = Build.DISPLAY
		RetrofitFactory.retrofit
				.create(XhuScheduleCloudAPI::class.java)
				.checkVersion(appVersion, systemVersion, manufacturer, model, rom, ConfigUtil.getDeviceID())
				.subscribeOn(Schedulers.io())
				.map { GsonFactory.parse<Version>(it) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Version>() {
					override fun onFinish(data: Version?) {
						if (data != null && data.versionCode.toInt() > getString(R.string.app_version_code).toInt())
							showUpdateDialog(data, intent.getBooleanExtra(CHECK_ACTION_BY_MANUAL, false))
						stopSelf()
						LocalBroadcastManager.getInstance(this@CheckUpdateService)
								.sendBroadcast(Intent(SettingsPreferenceFragment.ACTION_CHECK_UPDATE_DONE))
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						stopSelf()
					}
				})
		return super.onStartCommand(intent, flags, startId)
	}

	private fun showUpdateDialog(version: Version, checkByManual: Boolean) {
		if (!checkByManual) {
			val ignoreVersionList = ConfigurationUtil.ignoreUpdateVersion.split('!')
			if (ignoreVersionList.indexOf(version.versionCode) != -1)
				return
		}
		Observable.create<Boolean> {
			while (ActivityManagerTools.currentActivity() is SplashActivity || ActivityManagerTools.currentActivity() is GuideActivity || ActivityManagerTools.currentActivity() is SplashImageActivity)
				Thread.sleep(1000)
			it.onNext(ConfigurationUtil.autoCheckUpdate)
			it.onComplete()
		}
				.subscribeOn(Schedulers.single())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : OnlyCompleteObserver<Boolean>() {
					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
					}

					override fun onFinish(data: Boolean?) {
						if (data != null && data) {
							val activity = ActivityManagerTools.currentActivity() ?: return
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
									ActivityManagerTools.finishAllActivity()
								}
							else
								builder.setNeutralButton(R.string.action_download_cancel) { _, _ ->
									ConfigurationUtil.ignoreUpdateVersion = "${version.versionCode}!${ConfigurationUtil.ignoreUpdateVersion}"
								}
							builder.show()
						}
					}
				})
	}
}
