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
import com.weilylab.xhuschedule.api.QiniuAPI
import com.weilylab.xhuschedule.constant.IntentConstant
import com.weilylab.xhuschedule.utils.FileUtil
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.FileTools
import java.io.InputStream
import java.util.concurrent.TimeUnit

class DownloadSplashIntentService : IntentService("DownloadSplashIntentService") {
	private lateinit var retrofit: Retrofit

	override fun onCreate() {
		super.onCreate()
		val client = OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectTimeout(15, TimeUnit.SECONDS)
				.build()
		retrofit = Retrofit.Builder()
				.baseUrl("https://download.xhuschedule.mostpan.com")
				.client(client)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}

	override fun onHandleIntent(intent: Intent?) {
		if (intent == null)
			return
		val qiniuPath = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH) ?: return
		val objectId = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_SPLASH_FILE_NAME)
				?: return
		val file = FileUtil.getSplashImageFile(this, objectId) ?: return
		if (!file.parentFile.exists())
			file.parentFile.mkdirs()
		if (!file.exists())
			retrofit.create(QiniuAPI::class.java)
					.download(qiniuPath)
					.subscribeOn(Schedulers.newThread())
					.unsubscribeOn(Schedulers.newThread())
					.map { responseBody -> responseBody.byteStream() }
					.observeOn(Schedulers.io())
					.doOnNext { inputStream -> FileTools.saveFile(inputStream, file) }
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(object : Observer<InputStream> {
						override fun onComplete() {
							Logs.i("onComplete: ")
						}

						override fun onSubscribe(d: Disposable) {
							Logs.i("onSubscribe: ")
						}

						override fun onNext(t: InputStream) {
						}

						override fun onError(e: Throwable) {
							Logs.wtf("onError: ", e)
						}
					})
	}
}
