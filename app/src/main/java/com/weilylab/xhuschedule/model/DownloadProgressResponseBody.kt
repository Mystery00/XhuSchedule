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

package com.weilylab.xhuschedule.model

import com.weilylab.xhuschedule.listener.DownloadProgressListener
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * Created by JokAr-.
 * 原文地址：http://blog.csdn.net/a1018875550/article/details/51832700
 */
class DownloadProgressResponseBody(private val responseBody: ResponseBody,
								   private val progressListener: DownloadProgressListener?) : ResponseBody() {
	private var bufferedSource: BufferedSource? = null

	override fun contentType(): MediaType? = responseBody.contentType()

	override fun contentLength(): Long = responseBody.contentLength()

	override fun source(): BufferedSource {
		if (bufferedSource == null)
			bufferedSource = source(responseBody.source()).buffer()
		return bufferedSource!!
	}

	private fun source(source: Source): Source {
		return object : ForwardingSource(source) {
			var totalBytesRead = 0L

			@Throws(IOException::class)
			override fun read(sink: Buffer, byteCount: Long): Long {
				val bytesRead = super.read(sink, byteCount)
				totalBytesRead += if (bytesRead != -1L) bytesRead else 0
				progressListener?.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
				return bytesRead
			}
		}
	}
}