package com.weilylab.xhuschedule.util.cookie

import android.content.Context
import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * Created by myste.
 */
class SaveCookiesInterceptor(private val mContext: Context) : Interceptor
{

	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response
	{
		val request = chain.request()
		val response = chain.proceed(request)
		//set-cookie可能为多个
		if (!response.headers("set-cookie").isEmpty())
		{
			val cookies = response.headers("set-cookie")
			val cookie = encodeCookie(cookies)
			saveCookie(request.url().toString(), request.url().host(), cookie)
		}

		return response
	}

	//整合cookie为唯一字符串
	private fun encodeCookie(cookies: List<String>): String
	{
		val stringBuilder = StringBuilder()
		val set = HashSet<String>()
		cookies.map { cookie ->
			cookie.split(";".toRegex())
					.dropLastWhile { it.isEmpty() }
					.toTypedArray()
		}
				.forEach {
					it.filterNot { set.contains(it) }
							.forEach { set.add(it) }
				}

		val ite = set.iterator()
		while (ite.hasNext())
		{
			val cookie = ite.next()
			stringBuilder.append(cookie).append(";")
		}

		val last = stringBuilder.lastIndexOf(";")
		if (stringBuilder.length - 1 == last)
		{
			stringBuilder.deleteCharAt(last)
		}

		return stringBuilder.toString()
	}

	//保存cookie到本地，这里我们分别为该url和host设置相同的cookie，其中host可选
	//这样能使得该cookie的应用范围更广
	private fun saveCookie(url: String, domain: String, cookies: String)
	{
		val sharedPreferences = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE)
		val editor = sharedPreferences.edit()

		if (TextUtils.isEmpty(url))
		{
			throw NullPointerException("url is null.")
		}
		else
		{
			editor.putString(url, cookies)
		}

		if (!TextUtils.isEmpty(domain))
		{
			editor.putString(domain, cookies)
		}

		editor.apply()

	}

	companion object
	{
		private val COOKIE_PREF = "cookies_prefs"
	}
}