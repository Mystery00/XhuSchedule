package com.weilylab.xhuschedule.util

import android.app.Activity
import android.content.Context
import android.graphics.Point

/**
 * Created by myste.
 */
object DensityUtil
{
	fun dip2px(context: Context,
			   dpValue: Float): Int = (dpValue * context.resources.displayMetrics.density + 0.5F).toInt()

	fun px2dip(context: Context,
			   pxValue: Float): Int = (pxValue / context.resources.displayMetrics.density + 0.5F).toInt()

	fun getScreenWidth(activity: Activity):Int
	{
		val size = Point()
		activity.windowManager.defaultDisplay.getSize(size)
		return size.x
	}
}