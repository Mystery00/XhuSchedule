package com.weilylab.xhuschedule.util

import android.graphics.Color
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.util.cookie.CookieManger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.mystery0.tools.logs.Logs
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by myste.
 */
class ScheduleHelper private constructor()
{
	companion object
	{
		private val TAG = "ScheduleHelper"
		private var scheduleHelper: ScheduleHelper? = null

		fun getInstance(): ScheduleHelper
		{
			if (scheduleHelper == null)
				scheduleHelper = ScheduleHelper()
			return scheduleHelper!!
		}
	}

	var isLogin = false
	var isCookieAvailable = false
	private var client: OkHttpClient? = null
	private var retrofit: Retrofit? = null
	var studentName = "0"
	var studentNumber = "0"

	private fun getClient(): OkHttpClient
	{
		if (client == null)
		{
			client = OkHttpClient.Builder()
					.connectTimeout(10, TimeUnit.SECONDS)
					.readTimeout(10, TimeUnit.SECONDS)
					.writeTimeout(10, TimeUnit.SECONDS)
					.cookieJar(CookieManger(APP.getContext()))
					.build()
		}
		return client!!
	}

	fun getRetrofit(): Retrofit
	{
		if (retrofit == null)
		{
			retrofit = Retrofit.Builder()
					.client(getClient())
					.baseUrl("http://tomcat.weilylab.com:7823")
					.addConverterFactory(GsonConverterFactory.create())
					.build()
		}
		return retrofit!!
	}

	fun formatCourses(courses: Array<Course>): ArrayList<Course?>
	{
		val tempArray = Array(5, { Array<Course?>(7, { null }) })
		courses.forEach {
			val timeArray = it.time.split('-')
			val startTime = (timeArray[0].toInt() - 1) / 2
			val endTime = (timeArray[1].toInt()) / 2
			for (index in startTime until endTime)
			{
				if (tempArray[index][it.day.toInt() - 1] == null)
					tempArray[index][it.day.toInt() - 1] = it
				else
					tempArray[index][it.day.toInt() - 1]?.with(it)
			}
		}
		val list = ArrayList<Course?>()
		tempArray.forEach {
			it.forEach {
				list.add(it)
			}
		}
		return list
	}

	fun getWeekCourses(courses: Array<Course>): ArrayList<Course?>
	{
		val calendarUtil = CalendarUtil.getInstance()
		//开学时间
		calendarUtil.startCalendar.set(2017, 8, 4, 0, 0, 0)//月数减一
		val currentWeek = calendarUtil.getWeek()
		val tempArray = Array(5, { Array<Course?>(7, { null }) })
		courses.filter {
			try
			{
				val weekArray = it.week.split('-')
				val startWeek = weekArray[0].toInt()
				val endWeek = weekArray[1].toInt()
				var other = false
				when (it.type)
				{
					"0" -> other = true
					"1" -> if (currentWeek % 2 == 1)
						other = true
					"2" -> if (currentWeek % 2 == 0)
						other = true
					else -> other = false
				}
				currentWeek in startWeek..endWeek && other
			}
			catch (e: Exception)
			{
				false
			}
		}.forEach {
					val timeArray = it.time.split('-')
					val startTime = (timeArray[0].toInt() - 1) / 2
					val endTime = (timeArray[1].toInt()) / 2
					for (index in startTime until endTime)
					{
						if (tempArray[index][it.day.toInt() - 1] == null)
							tempArray[index][it.day.toInt() - 1] = it
						else
							tempArray[index][it.day.toInt() - 1]?.with(it)
					}
				}
		val list = ArrayList<Course?>()
		tempArray.forEach {
			it.forEach {
				list.add(it)
			}
		}
		return list
	}

	fun getTodayCourses(courses: Array<Course>): ArrayList<Course>
	{
		val calendarUtil = CalendarUtil.getInstance()
		//开学时间
		calendarUtil.startCalendar.set(2017, 8, 4, 0, 0, 0)//月数减一
		//获取当前第几周
		val currentWeek = calendarUtil.getWeek()
		val weekIndex = calendarUtil.getWeekIndex()
		val list = ArrayList<Course>()
		courses.filter {
			val weekArray = it.week.split('-')
			val startWeek = weekArray[0].toInt()
			val endWeek = weekArray[1].toInt()
			var other = false
			when (it.type)
			{
				"0" -> other = true
				"1" -> if (currentWeek % 2 == 1)
					other = true
				"2" -> if (currentWeek % 2 == 0)
					other = true
				else -> other = false
			}
			currentWeek in startWeek..endWeek && other && (it.day.toInt()) == weekIndex
		}
				.forEach {
					list.add(it)
				}
		return list
	}

	fun getRandomColor(): Int
	{
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
		return Color.parseColor("#33" + red + green + blue)
	}

	fun getMD5(message: String): String
	{
		try
		{
			val md5 = MessageDigest.getInstance("MD5")
			md5.update(message.toByteArray())
			val bigInteger = BigInteger(1, md5.digest())
			return bigInteger.toString(16)
		}
		catch (e: Exception)
		{
			e.printStackTrace()
		}
		return "ERROR"
	}
}