/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-4 下午5:21
 */

package com.weilylab.xhuschedule.listener

interface GetArrayListener<T>:BaseListener {
    fun got(array: Array<T>)
}