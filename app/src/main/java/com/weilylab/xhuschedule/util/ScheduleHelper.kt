/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.util.cookie.LoadCookiesInterceptor
import com.weilylab.xhuschedule.util.cookie.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by myste.
 */
object ScheduleHelper {
    var isLogin = false
    var isCookieAvailable = false
    var isImageChange = false
    var isUIChange = false
    var isAnalysisError = false
    var weekIndex = 0

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
            .baseUrl("https://mystery0.vip")
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
}