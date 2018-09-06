package com.weilylab.xhuschedule.interceptor

import com.weilylab.xhuschedule.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class DebugInterceptor : Interceptor {
	private lateinit var httpLoggingInterceptor: HttpLoggingInterceptor

	override fun intercept(chain: Interceptor.Chain): Response {
		if (BuildConfig.DEBUG) {
			if (!::httpLoggingInterceptor.isInitialized)
				httpLoggingInterceptor = HttpLoggingInterceptor()
						.setLevel(HttpLoggingInterceptor.Level.BODY)
			return httpLoggingInterceptor.intercept(chain)
		}
		return chain.proceed(chain.request())
	}
}