/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-21 上午12:24
 */

package com.weilylab.xhuschedule.util.cookie

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

class LoadCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val host = request.url().host()
        var username: String? = null
        when (request.method().toLowerCase()) {
            "get" -> {
                username = request.url().queryParameterValues("username")[0]
            }
            "post" -> {
                if (request.body() is FormBody) {
                    val formBody = request.body() as FormBody
                    username = (0 until formBody.size())
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