/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-17 下午4:53
 */

package com.weilylab.xhuschedule.listener

interface UploadLogListener : BaseListener {
    fun done(code: Int, message: String)
    fun ready()
}