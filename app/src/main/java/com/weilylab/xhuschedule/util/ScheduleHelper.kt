package com.weilylab.xhuschedule.util

import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.util.cookie.CookieManger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vip.mystery0.tools.logs.Logs
import java.util.concurrent.TimeUnit

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

	fun formatCourses(courses: Array<Course>): ArrayList<Course>
	{
		val tempArray = Array(5, { Array(7, { Course() }) })
		courses.forEach {
			val indexArray = it.time.split('-')
			val startIndex = (indexArray[0].toInt() - 1) / 2
			val endIndex = (indexArray[1].toInt()) / 2
			for (index in startIndex until endIndex)
			{
				if (tempArray[index][it.day.toInt() - 1].name == "")
					tempArray[index][it.day.toInt() - 1] = it
				else
					tempArray[index][it.day.toInt() - 1].with(it)
			}
		}
		val list = ArrayList<Course>()
		tempArray.forEach {
			it.forEach {
				list.add(it)
			}
		}
		return list
	}
}