package com.weilylab.xhuschedule.factory

import com.google.gson.Gson
import okhttp3.ResponseBody
import java.io.InputStream
import java.io.InputStreamReader

object GsonFactory {
	private val gson by lazy { Gson() }

	fun toJson(any: Any): String = gson.toJson(any)

	fun <T> parse(string: String, clazz: Class<T>): T = gson.fromJson(string, clazz)

	fun <T> parseInputStream(inputStream: InputStream, clazz: Class<T>): T = gson.fromJson(InputStreamReader(inputStream), clazz)

	inline fun <reified T> parse(inputStream: InputStream): T = parseInputStream(inputStream, T::class.java)

	inline fun <reified T> parse(responseBody: ResponseBody): T = parseInputStream(responseBody.byteStream(), T::class.java)
}