package com.weilylab.xhuschedule.util.download

import okhttp3.MediaType
import okhttp3.ResponseBody
import com.weilylab.xhuschedule.listener.DownloadProgressListener
import okio.*
import java.io.IOException


/**
 * Created by JokAr-.
 * 原文地址：http://blog.csdn.net/a1018875550/article/details/51832700
 */
class DownloadProgressResponseBody(private val responseBody: ResponseBody,
								   private val progressListener: DownloadProgressListener?) : ResponseBody()
{
	private var bufferedSource: BufferedSource? = null

	override fun contentType(): MediaType? = responseBody.contentType()

	override fun contentLength(): Long = responseBody.contentLength()

	override fun source(): BufferedSource
	{
		if (bufferedSource == null)
		{
			bufferedSource = Okio.buffer(source(responseBody.source()))
		}
		return bufferedSource!!
	}

	private fun source(source: Source): Source
	{
		return object : ForwardingSource(source)
		{
			internal var totalBytesRead = 0L

			@Throws(IOException::class)
			override fun read(sink: Buffer, byteCount: Long): Long
			{
				val bytesRead = super.read(sink, byteCount)
				// read() returns the number of bytes read, or -1 if this source is exhausted.
				totalBytesRead += if (bytesRead != -1L) bytesRead else 0
				progressListener?.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
				return bytesRead
			}
		}

	}
}