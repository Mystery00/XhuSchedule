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

package com.weilylab.xhuschedule.util

import android.annotation.TargetApi
import android.app.*
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.newPackage.interceptor.LoadCookiesInterceptor
import com.weilylab.xhuschedule.newPackage.interceptor.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.DensityTools
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Created by myste.
 */
object ScheduleHelper {
	private const val TAG = "ScheduleHelper"
	var isBackgroundChange = false
	var isAvatarChange = false
	var isUIChange = false
	var isTableLayoutChange = false
	var isAnalysisError = false
	var weekIndex = 0
	var scheduleItemWidth = -1

	val gson = Gson()

	private val client = OkHttpClient.Builder()
			.retryOnConnectionFailure(true)
			.connectTimeout(20, TimeUnit.SECONDS)
			.readTimeout(20, TimeUnit.SECONDS)
			.writeTimeout(20, TimeUnit.SECONDS)
			.addInterceptor(LoadCookiesInterceptor())
			.addInterceptor(SaveCookiesInterceptor())
			.build()

	val tomcatRetrofit = Retrofit.Builder()
			.baseUrl("https://xhuschedule.mostpan.com")
			.client(client)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!

	val phpRetrofit = Retrofit.Builder()
			.baseUrl("http://xhuschedule.mostpan.com:9783")
			.client(client)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!

	val imgRetrofit = Retrofit.Builder()
			.baseUrl("http://download.xhuschedule.mostpan.com")
			.client(client)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()!!

	fun getMD5(message: String): String {
		try {
			val md5 = MessageDigest.getInstance("MD5")
			md5.update(message.toByteArray())
			val bigInteger = BigInteger(1, md5.digest())
			return bigInteger.toString(16)
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return "ERROR"
	}

	fun initChannelID(context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(createDefaultChannel())
			notificationManager.createNotificationChannel(createChannel(Constants.NOTIFICATION_CHANNEL_ID_DOWNLOAD, Constants.NOTIFICATION_CHANNEL_NAME_DOWNLOAD, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_DOWNLOAD, NotificationManager.IMPORTANCE_LOW))
			notificationManager.createNotificationChannel(createChannel(Constants.NOTIFICATION_CHANNEL_ID_TOMORROW, Constants.NOTIFICATION_CHANNEL_NAME_TOMORROW, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_TOMORROW, NotificationManager.IMPORTANCE_HIGH))
			notificationManager.createNotificationChannel(createChannel(Constants.NOTIFICATION_CHANNEL_ID_PUSH, Constants.NOTIFICATION_CHANNEL_NAME_PUSH, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_PUSH, NotificationManager.IMPORTANCE_DEFAULT))
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private fun createDefaultChannel(): NotificationChannel {
		return createChannel(Constants.NOTIFICATION_CHANNEL_ID_DEFAULT, Constants.NOTIFICATION_CHANNEL_NAME_DEFAULT, null, NotificationManager.IMPORTANCE_LOW)
	}

	@TargetApi(Build.VERSION_CODES.O)
	private fun createChannel(channelID: String, channelName: String, channelDescription: String?, importance: Int): NotificationChannel {
		val channel = NotificationChannel(channelID, channelName, importance)
		channel.enableLights(true)
		channel.description = channelDescription
		channel.lightColor = Color.GREEN
		return channel
	}
}