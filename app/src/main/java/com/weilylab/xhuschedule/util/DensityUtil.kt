/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:50
 */

package com.weilylab.xhuschedule.util

import android.app.Activity
import android.content.Context
import android.graphics.Point


/**
 * Created by myste.
 */
object DensityUtil {
    fun dip2px(context: Context,
               dpValue: Float): Int = (dpValue * context.resources.displayMetrics.density + 0.5F).toInt()

    fun px2dip(context: Context,
               pxValue: Float): Int = (pxValue / context.resources.displayMetrics.density + 0.5F).toInt()

    fun getScreenWidth(activity: Activity): Int {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }
}