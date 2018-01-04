/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-5 上午1:10
 */

package com.weilylab.xhuschedule.listener

import com.weilylab.xhuschedule.classes.baseClass.Score

interface GetScoreListener :BaseListener{
    fun got(array: Array<Score>, failedArray: Array<Score>)
}