package com.weilylab.xhuschedule.util

import android.content.Context
import android.graphics.Bitmap



/**
 * Created by myste.
 */
object DensityUtil {
    fun dip2px(context: Context,
               dpValue: Float): Int = (dpValue * context.resources.displayMetrics.density + 0.5F).toInt()

    fun px2dip(context: Context,
               pxValue: Float): Int = (pxValue / context.resources.displayMetrics.density + 0.5F).toInt()

    fun getBright(bitmap: Bitmap): Int {
        val width = bitmap.width
        val height = bitmap.height
        var r: Int
        var g: Int
        var b: Int
        var count = 0
        var bright = 0
        for (i in 0 until width) {
            for (j in 0 until height) {
                count++
                val localTemp = bitmap.getPixel(i, j)
                r = localTemp or -0xff0001 shr 16 and 0x00ff
                g = localTemp or -0xff01 shr 8 and 0x0000ff
                b = localTemp or -0x100 and 0x0000ff
                bright = (bright.toDouble() + 0.299 * r + 0.587 * g + 0.114 * b).toInt()
            }
        }
        return bright / count
    }
}