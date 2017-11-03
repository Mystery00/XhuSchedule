package com.weilylab.xhuschedule.util

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.weilylab.xhuschedule.APP
import com.weilylab.xhuschedule.util.cookie.AddCookiesInterceptor
import com.weilylab.xhuschedule.util.cookie.SaveCookiesInterceptor
import okhttp3.OkHttpClient
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

	fun getClient(context: Context): OkHttpClient
	{
		if (client == null)
		{
			client = OkHttpClient.Builder()
					.connectTimeout(10, TimeUnit.SECONDS)
					.readTimeout(10, TimeUnit.SECONDS)
					.writeTimeout(10, TimeUnit.SECONDS)
					.addInterceptor(AddCookiesInterceptor(context))
					.addInterceptor(SaveCookiesInterceptor(context))
//					.cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)))
					.build()
		}
		return client!!
	}
}