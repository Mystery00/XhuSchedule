/*
 * Created by Mystery0 on 18-1-4 下午8:25.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-1-4 下午8:25
 */

package com.weilylab.xhuschedule.listener

import com.weilylab.xhuschedule.classes.baseClass.ExpScore

interface GetExpScoreListener:BaseListener {
    fun got(array: Array<ExpScore>)
}