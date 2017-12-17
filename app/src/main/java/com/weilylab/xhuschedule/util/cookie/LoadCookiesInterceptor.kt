/*
 * Created by Mystery0 on 17-11-30 上午9:18.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-30 上午9:18
 */

package com.weilylab.xhuschedule.util.cookie

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

class LoadCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        if (request.body() is FormBody){
            val formBody = request.body() as FormBody
            val host = request.url().host()
            val username: String? = (0 until formBody.size())
                    .firstOrNull { formBody.encodedName(it) == "username" }
                    ?.let { formBody.encodedValue(it) }
            if (username != null && CookieManger.getCookie(username, host) != null) {
                builder.addHeader("Cookie", CookieManger.getCookie(username, host)!!)
            }
        }
        return chain.proceed(builder.build())
    }
}