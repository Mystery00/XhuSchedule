package com.weilylab.xhuschedule.interceptor

import com.weilylab.xhuschedule.utils.ConfigurationUtil
import okhttp3.Interceptor
import okhttp3.Response

class JRSCInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		val builder = request.newBuilder()
		if (request.method().toLowerCase() == "get") {
			if (request.url().toString().contains("v2.jinrishici.com")) {
				builder.addHeader("X-User-Token", ConfigurationUtil.jrscToken)
			}
		}
		return chain.proceed(builder.build())
	}
}