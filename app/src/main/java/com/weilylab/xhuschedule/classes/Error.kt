/*
 * Created by Mystery0 on 17-11-27 下午9:53.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 下午9:53
 */

package com.weilylab.xhuschedule.classes

import java.io.Serializable

/**
 * Created by myste.
 */
data class Error(val time: String, val appVersionName: String,
                 val appVersionCode: Int, val AndroidVersion: String,
                 val sdk: Int, val vendor: String, val model: String,
                 val ex: Throwable) : Serializable