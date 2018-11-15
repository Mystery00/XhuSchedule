package com.weilylab.xhuschedule.interceptor

import com.weilylab.xhuschedule.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class DebugInterceptor : Interceptor {
	private val httpLoggingInterceptor by lazy {
		HttpLoggingInterceptor()
				.setLevel(HttpLoggingInterceptor.Level.BODY)
	}

	override fun intercept(chain: Interceptor.Chain): Response = if (BuildConfig.DEBUG)
		httpLoggingInterceptor.intercept(chain)
	else
		chain.proceed(chain.request())
}