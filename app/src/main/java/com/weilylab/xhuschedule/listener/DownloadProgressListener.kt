/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-21 下午2:26
 */

package com.weilylab.xhuschedule.listener

/**
 * Created by myste.
 */
interface DownloadProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}