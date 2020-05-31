/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.interceptor

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class LoadCookiesInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		val builder = request.newBuilder()
		val host = request.url.host
		var username: String? = null
		when (request.method.toLowerCase(Locale.CHINA)) {
			"get" -> {
				val list = request.url.queryParameterValues("username")
				username = if (list.isNotEmpty()) list[0] else null
			}
			"post" -> {
				if (request.body is FormBody) {
					val formBody = request.body as FormBody
					username = (0 until formBody.size)
							.firstOrNull { formBody.encodedName(it) == "username" }
							?.let { formBody.encodedValue(it) }
				}
			}
		}
		if (username != null && CookieManger.getCookie(username, host) != null) {
			builder.addHeader("Cookie", CookieManger.getCookie(username, host)!!)
		}
		return chain.proceed(builder.build())
	}
}