/*
 * Created by Mystery0 on 17-12-4 下午5:16.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-4 下午5:16
 */

package com.weilylab.xhuschedule.listener

interface BaseListener {
    fun error(rt: Int, e:Throwable)
    fun doInThread()
}