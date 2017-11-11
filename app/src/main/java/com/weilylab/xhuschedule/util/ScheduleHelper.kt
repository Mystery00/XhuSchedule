package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.util.cookie.CookieManger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.mystery0.tools.logs.Logs
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
		val startCalendar = Calendar.getInstance()
		//开学时间
		startCalendar.set(2017, 8, 4)//月数减一
		val currentCalendar = Calendar.getInstance()
		startCalendar.firstDayOfWeek = Calendar.MONDAY
		currentCalendar.firstDayOfWeek = Calendar.MONDAY
		//获取当前第几周---加一获取正确周数
		val currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR) - startCalendar.get(Calendar.WEEK_OF_YEAR) + 1
		val tempArray = Array(5, { Array<Course?>(7, { null }) })
		courses.filter {
			val weekArray = it.week.split('-')
			val startWeek = weekArray[0].toInt()
			val endWeek = weekArray[1].toInt()
			currentWeek in startWeek..endWeek
		}
				.forEach {
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
		val startCalendar = Calendar.getInstance()
		//开学时间
		startCalendar.set(2017, 8, 4)
		val currentCalendar = Calendar.getInstance()
		//当前星期几
		val weekIndex = currentCalendar.get(Calendar.DAY_OF_WEEK) - 1
		startCalendar.firstDayOfWeek = Calendar.MONDAY
		currentCalendar.firstDayOfWeek = Calendar.MONDAY
		//获取当前第几周
		val currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR) - startCalendar.get(Calendar.WEEK_OF_YEAR) + 1
		val list = ArrayList<Course>()
		courses.filter {
			val weekArray = it.week.split('-')
			val startWeek = weekArray[0].toInt()
			val endWeek = weekArray[1].toInt()
			currentWeek in startWeek..endWeek && (it.day.toInt() - 1) == weekIndex
		}
				.forEach {
					list.add(it)
				}
		return list
	}
}