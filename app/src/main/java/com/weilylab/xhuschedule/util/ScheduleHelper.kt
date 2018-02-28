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
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.receiver.AlarmReceiver
import com.weilylab.xhuschedule.util.cookie.LoadCookiesInterceptor
import com.weilylab.xhuschedule.util.cookie.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import vip.mystery0.tools.logs.Logs
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by myste.
 */
object ScheduleHelper {
    private const val TAG = "ScheduleHelper"
    var isImageChange = false
    var isUIChange = false
    var isTableLayoutChange = false
    var isAnalysisError = false
    var weekIndex = 0
    var scheduleItemWidth = -1

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

    fun getRandomColor(): String {
        val random = Random()
        //生成红色颜色代码
        var red: String = Integer.toHexString(random.nextInt(180) + 40).toUpperCase()
        //生成绿色颜色代码
        var green: String = Integer.toHexString(random.nextInt(100) + 90).toUpperCase()
        //生成蓝色颜色代码
        var blue: String = Integer.toHexString(random.nextInt(120) + 120).toUpperCase()
        //判断红色代码的位数
        red = if (red.length == 1) "0" + red else red
        //判断绿色代码的位数
        green = if (green.length == 1) "0" + green else green
        //判断蓝色代码的位数
        blue = if (blue.length == 1) "0" + blue else blue
        //生成十六进制颜色值
        return red + green + blue
    }

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
        Logs.i(TAG, "initChannelID: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(createDefaultChannel())
            notificationManager.createNotificationChannel(createChannel(Constants.NOTIFICATION_CHANNEL_ID_DOWNLOAD, Constants.NOTIFICATION_CHANNEL_NAME_DOWNLOAD, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_DOWNLOAD, NotificationManager.IMPORTANCE_LOW))
            notificationManager.createNotificationChannel(createChannel(Constants.NOTIFICATION_CHANNEL_ID_TOMORROW, Constants.NOTIFICATION_CHANNEL_NAME_TOMORROW, Constants.NOTIFICATION_CHANNEL_DESCRIPTION_TOMORROW, NotificationManager.IMPORTANCE_HIGH))
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

    fun setTrigger(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
        alarmManager.cancel(pendingIntent)//关闭定时器
        if (!Settings.isNotificationTomorrowEnable && Settings.isNotificationExamEnable)
            return
        //设置定时器
        val triggerAtTime = CalendarUtil.getNotificationTriggerTime()
        if (Settings.notificationExactTime)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent)
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent)
    }

    fun checkScreenWidth(context: Context) {
        scheduleItemWidth = if (Settings.customTableItemWidth != -1)
            DensityUtil.dip2px(context, Settings.customTableItemWidth.toFloat())
        else {
            val navWidth = context.resources.getDimensionPixelSize(R.dimen.nav_width)
            val lineWidth = context.resources.getDimensionPixelSize(R.dimen.divider_size)
            (DensityUtil.getScreenWidth(context) - navWidth - lineWidth) / 7
        }
    }
}