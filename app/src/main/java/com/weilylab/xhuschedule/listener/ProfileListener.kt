/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-4 下午5:21
 */

package com.weilylab.xhuschedule.listener

import com.weilylab.xhuschedule.classes.baseClass.Profile

interface ProfileListener:BaseListener {
    fun got(profile: Profile)
}