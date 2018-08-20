/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.interceptor

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
				if (request.url().toString().contains("api.lncld.net/1.1/classes/")) {
					builder.addHeader("X-LC-Id", "f939kTGhlyHAHVsCpccyWU6t-gzGzoHsz")
					builder.addHeader("X-LC-Key", "wVpJkuI4DLNxXHBfp19XGz3E")
					builder.addHeader("Content-Type", "application/json")
					return chain.proceed(builder.build())
				}
				val list = request.url().queryParameterValues("username")
				username = if (list.isNotEmpty()) list[0] else null
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