/*
 * Created by Mystery0 on 17-12-4 下午4:27.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-4 下午4:27
 */

package com.weilylab.xhuschedule.listener

interface GetArrayListener<T>:BaseListener {
    fun got(array: Array<T>)
}