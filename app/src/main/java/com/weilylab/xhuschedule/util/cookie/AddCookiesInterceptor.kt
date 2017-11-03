package com.weilylab.xhuschedule.util.cookie

import android.content.Context
import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * Created by myste.
 */
class AddCookiesInterceptor(private val mContext: Context) : Interceptor
{

	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response
	{
		val request = chain.request()
		val builder = request.newBuilder()
		val cookie = getCookie(request.url().toString(), request.url().host())
		if (!TextUtils.isEmpty(cookie))
		{
			builder.addHeader("Cookie", cookie)
		}

		return chain.proceed(builder.build())
	}

	private fun getCookie(url: String, domain: String): String?
	{
		val sharedPreferences = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE)
		if (!TextUtils.isEmpty(url) && sharedPreferences.contains(url) && !TextUtils.isEmpty(sharedPreferences.getString(url, "")))
		{
			return sharedPreferences.getString(url, "")
		}
		return if (!TextUtils.isEmpty(domain) && sharedPreferences.contains(domain) && !TextUtils.isEmpty(sharedPreferences.getString(domain, "")))
		{
			sharedPreferences.getString(domain, "")
		}
		else null

	}

	companion object
	{
		private val COOKIE_PREF = "cookies_prefs"
	}
}