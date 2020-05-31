/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.factory

import okhttp3.ResponseBody
import vip.mystery0.tools.factory.GsonFactory
import java.io.InputStream
import java.io.InputStreamReader

inline fun <reified T> InputStream.fromJson(): T = GsonFactory.gson.fromJson(InputStreamReader(this), T::class.java)

inline fun <reified T> ResponseBody.fromJson(): T = this.byteStream().fromJson()