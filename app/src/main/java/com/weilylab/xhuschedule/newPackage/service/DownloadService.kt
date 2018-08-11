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

package com.weilylab.xhuschedule.newPackage.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import android.text.TextUtils
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.api.QiniuAPI
import com.weilylab.xhuschedule.listener.DownloadProgressListener
import com.weilylab.xhuschedule.util.BsPatch
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.notification.DownloadNotification
import com.weilylab.xhuschedule.util.XhuFileUtil
import com.weilylab.xhuschedule.util.download.Download
import com.weilylab.xhuschedule.util.download.DownloadProgressInterceptor
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import vip.mystery0.logs.Logs
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Created by myste.
 */
class DownloadService : IntentService("DownloadService") {
	companion object {
		fun intentTo(context: Context, type: String, qiniuPath: String) {
			val intent = Intent(context, DownloadService::class.java)
			intent.putExtra(Constants.INTENT_TAG_NAME_TYPE, type)
			intent.putExtra(Constants.INTENT_TAG_NAME_QINIU_PATH, qiniuPath)
			context.startService(intent)
		}
	}

	private lateinit var retrofit: Retrofit

	override fun onHandleIntent(intent: Intent?) {
		Logs.i("onHandleIntent: ")
		val type = intent?.getStringExtra(Constants.INTENT_TAG_NAME_TYPE)
		val qiniuPath = intent?.getStringExtra(Constants.INTENT_TAG_NAME_QINIU_PATH)
		val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + File.separator + qiniuPath)
		if (!file.parentFile.exists())
			file.parentFile.mkdirs()
		if (TextUtils.isEmpty(type) || TextUtils.isEmpty(qiniuPath)) {
			Logs.i("onHandleIntent: 格式错误")
			return
		}
		Logs.i("onStartCommand: type: $type")
		Logs.i("onStartCommand: qiniuPath: $qiniuPath")
		Logs.i("onStartCommand: " + file.absolutePath)
		download(this, type!!, qiniuPath!!, file)
	}

	override fun onCreate() {
		Logs.i("onCreate: ")
		super.onCreate()

		val listener = object : DownloadProgressListener {
			private var temp = 0

			override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
				val download = Download()
				download.totalFileSize = contentLength
				download.currentFileSize = bytesRead
				download.progress = (bytesRead * 100 / contentLength).toInt()
				if (temp % 3 == 0)
					DownloadNotification.updateProgress(applicationContext, download)
				temp++
			}
		}
		val client = OkHttpClient.Builder()
				.addInterceptor(DownloadProgressInterceptor(listener))
				.retryOnConnectionFailure(true)
				.connectTimeout(15, TimeUnit.SECONDS)
				.build()
		retrofit = Retrofit.Builder()
				.baseUrl("https://download.xhuschedule.mostpan.com")
				.client(client)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}

	private fun download(context: Context, type: String, qiniuPath: String, file: File) {
		Logs.i("download: $qiniuPath")
		retrofit.create(QiniuAPI::class.java)
				.download(qiniuPath)
				.subscribeOn(Schedulers.newThread())
				.unsubscribeOn(Schedulers.newThread())
				.map { responseBody -> responseBody.byteStream() }
				.observeOn(Schedulers.io())
				.doOnNext { inputStream ->
					try {
						XhuFileUtil.saveFile(inputStream, file)
						if (type == Constants.DOWNLOAD_TYPE_PATCH) {
							val applicationInfo = applicationContext.applicationInfo
							Logs.i("patchAPK: " + applicationInfo.sourceDir)
							val newApkPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + File.separator + "apk" + File.separator + qiniuPath + ".apk"
							val newAPK = File(newApkPath)
							if (!newAPK.parentFile.exists())
								newAPK.parentFile.mkdirs()
							BsPatch.patch(applicationInfo.sourceDir,
									newApkPath,
									file.absolutePath)
						}
					} catch (e: IOException) {
						e.printStackTrace()
					}
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Observer<InputStream> {
					override fun onSubscribe(d: Disposable) {
						Logs.i("onSubscribe: ")
						DownloadNotification.notify(context, qiniuPath)
					}

					override fun onComplete() {
						val installIntent = Intent(Intent.ACTION_VIEW)
						installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
						installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
						val installFile = if (type == Constants.DOWNLOAD_TYPE_PATCH)
							File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + File.separator + "apk" + File.separator + qiniuPath + ".apk")
						else
							file
						val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
							FileProvider.getUriForFile(context, getString(R.string.uri_authority), installFile)
						else
							Uri.fromFile(installFile)
						Logs.i("onComplete: " + installFile.absolutePath)
						Logs.i("onComplete: $uri")
						installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
						startActivity(installIntent)
						DownloadNotification.cancel(context)
						stopSelf()
					}

					override fun onNext(t: InputStream) {
					}

					override fun onError(e: Throwable) {
						DownloadNotification.downloadError(context)
						e.printStackTrace()
					}
				})
	}

	override fun onTaskRemoved(rootIntent: Intent?) {
		DownloadNotification.cancel(this)
	}
}