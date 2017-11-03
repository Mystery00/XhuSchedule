package com.weilylab.xhuschedule.util

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.weilylab.xhuschedule.APP
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
	var client: OkHttpClient = OkHttpClient.Builder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(APP.getContext())))
			.build()
}