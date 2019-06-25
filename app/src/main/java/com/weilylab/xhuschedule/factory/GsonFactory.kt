package com.weilylab.xhuschedule.factory

import okhttp3.ResponseBody
import vip.mystery0.tools.factory.GsonFactory
import java.io.InputStream
import java.io.InputStreamReader

inline fun <reified T> InputStream.fromJson(): T = GsonFactory.gson.fromJson(InputStreamReader(this), T::class.java)

inline fun <reified T> ResponseBody.fromJson(): T = this.byteStream().fromJson()