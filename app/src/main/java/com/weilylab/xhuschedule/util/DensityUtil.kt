/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午3:50
 */

package com.weilylab.xhuschedule.util

import android.content.Context


/**
 * Created by myste.
 */
object DensityUtil {
    fun dip2px(context: Context,
               dpValue: Float): Int = (dpValue * context.resources.displayMetrics.density + 0.5F).toInt()

    fun px2dip(context: Context,
               pxValue: Float): Int = (pxValue / context.resources.displayMetrics.density + 0.5F).toInt()

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getWidth(context: Context, leftMarginDip: Float, rightMarginDip: Float): Int {
        return getScreenWidth(context) - dip2px(context, leftMarginDip) - dip2px(context, rightMarginDip)
    }
}