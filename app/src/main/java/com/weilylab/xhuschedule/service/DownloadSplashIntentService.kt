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
import com.weilylab.xhuschedule.utils.getSplashImageFile
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.copyToFile
import vip.mystery0.tools.utils.md5

class DownloadSplashIntentService : IntentService("DownloadSplashIntentService") {
	companion object {
		private val TAG = "DownloadSplashIntentService"
	}

	private val qiniuAPI: QiniuAPI by inject()

	override fun onHandleIntent(intent: Intent?) {
		if (intent == null)
			return
		val qiniuPath = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_QINIU_PATH) ?: return
		val objectId = intent.getStringExtra(IntentConstant.INTENT_TAG_NAME_SPLASH_FILE_NAME)
				?: return
		val file = getSplashImageFile(objectId) ?: return
		if (!file.parentFile!!.exists())
			file.parentFile!!.mkdirs()
		if (!file.exists())
			GlobalScope.launch(CoroutineExceptionHandler { _, throwable ->
				Logs.wtf(TAG, "download: ", throwable)
			}) {
				withContext(Dispatchers.IO) {
					val body = qiniuAPI.download(qiniuPath)
					body.byteStream().copyToFile(file)
					file.md5()
				}
			}
	}
}
