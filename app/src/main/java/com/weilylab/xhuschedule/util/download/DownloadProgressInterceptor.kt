/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-21 下午2:26
 */

package com.weilylab.xhuschedule.util.download

import com.weilylab.xhuschedule.listener.DownloadProgressListener
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by JokAr-.
 * 原文地址：http://blog.csdn.net/a1018875550/article/details/51832700
 */
class DownloadProgressInterceptor(private val listener: DownloadProgressListener) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        return originalResponse.newBuilder()
                .body(DownloadProgressResponseBody(originalResponse.body()!!, listener))
                .build()
    }
}