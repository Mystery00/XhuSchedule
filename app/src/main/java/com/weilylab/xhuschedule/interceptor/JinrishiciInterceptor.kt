/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.interceptor

import com.weilylab.xhuschedule.config.JRSCConfig
import com.weilylab.xhuschedule.constant.Constants
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class JinrishiciInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		val builder = request.newBuilder()
		if (request.method.toLowerCase(Locale.CHINA) == "get" && request.url.toString().contains(Constants.SERVER_JRSC)) {
			if (request.url.toString().contains("one.json")) { //请求今日诗词的接口
				val token = JRSCConfig.token
				if (token == null) throw Exception("请求今日诗词出错")
				else builder.addHeader("X-User-Token", token)
			}
		}
		return chain.proceed(builder.build())
	}
}