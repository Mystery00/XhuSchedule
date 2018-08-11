package com.weilylab.xhuschedule.newPackage.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.api.PhpAPI
import com.weilylab.xhuschedule.newPackage.factory.GsonFactory
import com.weilylab.xhuschedule.newPackage.factory.RetrofitFactory
import com.weilylab.xhuschedule.newPackage.model.Version
import com.weilylab.xhuschedule.newPackage.utils.APPActivityManager
import com.weilylab.xhuschedule.newPackage.utils.rxAndroid.RxObserver
import com.weilylab.xhuschedule.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.FileTools

class CheckUpdateService : Service() {
	override fun onBind(intent: Intent): IBinder? = null

	override fun onCreate() {
		super.onCreate()
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_DEFAULT)
				.setSmallIcon(R.drawable.ic_stat_push_message)
				.setContentText(getString(R.string.hint_foreground_notification))
				.setAutoCancel(true)
				.setPriority(NotificationManagerCompat.IMPORTANCE_NONE)
				.build()
		startForeground(0, notification)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		RetrofitFactory.retrofit
				.create(PhpAPI::class.java)
				.checkVersion()
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { GsonFactory.parseInputStream(it.byteStream(), Version::class.java) }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : RxObserver<Version>() {
					override fun onFinish(data: Version?) {
//						if (data != null && data.versionCode.toInt() > getString(R.string.app_version_code).toInt())
						if (data != null)
							showUpdateDialog(data)
						Thread(Runnable {
							Thread.sleep(10000)
							stopSelf()
						}).start()
					}

					override fun onError(e: Throwable) {
						Logs.wtf("onError: ", e)
						stopSelf()
					}
				})
		return super.onStartCommand(intent, flags, startId)
	}

	private fun showUpdateDialog(version: Version) {
		val activity = APPActivityManager.currentActivity() ?: return
		val title = getString(R.string.dialog_update_title, getString(R.string.app_version_name), version.versionName)
		val text = getString(R.string.dialog_update_text, version.updateLog)
		val builder = AlertDialog.Builder(activity)
				.setTitle(title)
				.setMessage(text)
				.setPositiveButton("${getString(R.string.action_download_apk)}(${FileTools.formatFileSize(version.apkSize.toLong())})") { _, _ ->
					Logs.i("showUpdateDialog: 下载")
					DownloadService.intentTo(activity, Constants.DOWNLOAD_TYPE_APK, version.apkQiniuPath)
				}
		if (version.lastVersionCode == getString(R.string.app_version_code))
			builder.setNegativeButton("${getString(R.string.action_download_patch)}(${FileTools.formatFileSize(version.patchSize.toLong())})") { _, _ ->
				Logs.i("showUpdateDialog: 下载增量")
				DownloadService.intentTo(activity, Constants.DOWNLOAD_TYPE_PATCH, version.patchQiniuPath)
			}
		if (version.must == "1")
			builder.setOnCancelListener {
				APPActivityManager.finishAllActivity()
			}
		else
			builder.setNeutralButton(R.string.action_download_cancel) { _, _ ->
				Logs.i("showUpdateDialog: 忽略")
			}
		if (APPActivityManager.currentActivity() != null)
			builder.show()
	}
}
