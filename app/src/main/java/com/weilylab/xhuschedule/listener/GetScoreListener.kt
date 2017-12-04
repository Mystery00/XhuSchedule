/*
 * Created by Mystery0 on 17-12-5 上午1:04.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-5 上午1:04
 */

package com.weilylab.xhuschedule.listener

import com.weilylab.xhuschedule.classes.Score

interface GetScoreListener :BaseListener{
    fun got(array: Array<Score>,failedArray: Array<Score>)
}