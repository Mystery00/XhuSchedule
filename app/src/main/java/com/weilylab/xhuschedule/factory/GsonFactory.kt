package com.weilylab.xhuschedule.factory

import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader

object GsonFactory {
	val gson = Gson()

	fun <T> parseInputStream(inputStream: InputStream, clazz: Class<T>): T = gson.fromJson(InputStreamReader(inputStream), clazz)
}