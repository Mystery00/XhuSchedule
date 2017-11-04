package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.util.cookie.CookieManger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by myste.
 */
class ScheduleHelper private constructor()
{
	companion object
	{
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
}