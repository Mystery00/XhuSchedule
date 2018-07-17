/*
 * Created by Mystery0 on 4/3/18 5:09 PM.
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
 * Last modified 4/3/18 5:09 PM
 */

package com.weilylab.xhuschedule.service

import android.app.IntentService
import android.content.Intent
import com.weilylab.xhuschedule.interfaces.QiniuService
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import vip.mystery0.logs.Logs
import java.io.InputStream
import java.util.concurrent.TimeUnit

class DownloadSplashIntentService : IntentService(TAG) {
	companion object {
		private const val TAG = "DownloadSplashIntentService"
	}

	private lateinit var retrofit: Retrofit

	override fun onCreate() {
		super.onCreate()
		val client = OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectTimeout(15, TimeUnit.SECONDS)
				.build()
		retrofit = Retrofit.Builder()
				.baseUrl("http://download.xhuschedule.mostpan.com")
				.client(client)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}

	override fun onHandleIntent(intent: Intent?) {
		val qiniuPath = intent?.getStringExtra(Constants.INTENT_TAG_NAME_QINIU_PATH) ?: return
		val objectId = intent.getStringExtra(Constants.INTENT_TAG_NAME_SPLASH_FILE_NAME)
				?: return
		val file = XhuFileUtil.getSplashImageFile(this, objectId) ?: return
		Logs.i(TAG, "onHandleIntent: $objectId")
		Logs.i(TAG, "onHandleIntent: ${file.absolutePath}")
		if (!file.parentFile.exists())
			file.parentFile.mkdirs()
		if (file.exists())
			Settings.splashImage = objectId
		else
			retrofit.create(QiniuService::class.java)
					.download(qiniuPath)
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.map { responseBody -> responseBody.byteStream() }
					.observeOn(Schedulers.io())
					.doOnNext { inputStream ->
						XhuFileUtil.saveFile(inputStream, file)
					}
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Observer<InputStream> {
						override fun onComplete() {
							Logs.i(TAG, "onComplete: ")
							Settings.splashImage = objectId
						}

						override fun onSubscribe(d: Disposable) {
							Logs.i(TAG, "onSubscribe: ")
						}

						override fun onNext(t: InputStream) {
						}

						override fun onError(e: Throwable) {
							Logs.wtf(TAG, "onError: ", e)
						}
					})
	}
}
