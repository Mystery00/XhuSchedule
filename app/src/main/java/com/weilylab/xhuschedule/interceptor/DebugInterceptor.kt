/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.interceptor

import com.weilylab.xhuschedule.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class DebugInterceptor : Interceptor {
	private val httpLoggingInterceptor by lazy {
		val interceptor = HttpLoggingInterceptor()
		interceptor.level = HttpLoggingInterceptor.Level.BASIC
		interceptor
	}

	override fun intercept(chain: Interceptor.Chain): Response = if (BuildConfig.DEBUG)
		httpLoggingInterceptor.intercept(chain)
	else
		chain.proceed(chain.request())
}