package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.util.cookie.CookieManger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null
    private var updateRetrofit: Retrofit? = null
    var studentName = "0"
    var studentNumber = "0"
    var weekIndex = 0
    var itemCourseWidth = 0F//课程宽度
    var itemCourseNameSize = 0F
    var itemCourseTeacherSize = 0F
    var itemCourseLocationSize = 0F

    private fun getClient(): OkHttpClient {
        if (client == null) {
            client = OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .cookieJar(CookieManger(APP.getContext()))
                    .build()
        }
        return client!!
    }

    fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .client(getClient())
                    .baseUrl("http://tomcat.weilylab.com:7823")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!
    }

    fun getUpdateRetrofit(): Retrofit {
        if (updateRetrofit == null) {
            updateRetrofit = Retrofit.Builder()
                    .client(getClient())
                    .baseUrl("http://tomcat.weilylab.com:9783")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return updateRetrofit!!
    }

    fun getRandomColor(): String {
        val random = Random()
        //生成红色颜色代码
        var red: String = Integer.toHexString(random.nextInt(256)).toUpperCase()
        //生成绿色颜色代码
        var green: String = Integer.toHexString(random.nextInt(256)).toUpperCase()
        //生成蓝色颜色代码
        var blue: String = Integer.toHexString(random.nextInt(256)).toUpperCase()
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