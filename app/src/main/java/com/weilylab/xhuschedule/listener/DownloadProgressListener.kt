package com.weilylab.xhuschedule.listener

/**
 * Created by myste.
 */
interface DownloadProgressListener
{
	fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}