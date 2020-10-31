/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils

import android.content.Context
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import java.util.*

object WidgetUtil {
    fun saveWidgetIDs(context: Context, name: String, appWidgetIds: IntArray) {
        val sharedPreferences = context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_IDS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putStringSet(name, appWidgetIds.map { it.toString() }.toSet())
                .apply()
    }

    fun getWidgetIDs(context: Context, name: String): IntArray {
        val sharedPreferences = context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_IDS, Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(name, HashSet<String>())!!.map { it.toInt() }
                .toIntArray()
    }

    fun getColor(color: Color): Int {
        when (color) {
            Color.GrayBackground ->
                return if (isNight())
                    parse("#212121")
                else
                    parse("#fafafa")
            Color.WhiteBackground ->
                return if (isNight())
                    parse("#242424")
                else
                    parse("#FFFFFF")
            Color.BlackText ->
                return if (isNight())
                    parse("#FFFFFF")
                else
                    parse("#FF000000")
            Color.GrayText ->
                return if (isNight())
                    parse("#FFFFFF")
                else
                    parse("#8A000000")
        }
    }

    private fun parse(colorString: String): Int = android.graphics.Color.parseColor(colorString)

    private fun isNight(): Boolean = when (ConfigurationUtil.nightMode) {
        0 -> Calendar.getInstance()[Calendar.HOUR_OF_DAY] !in 7..19
        1 -> true
        2 -> false
        else -> Calendar.getInstance()[Calendar.HOUR_OF_DAY] !in 7..19
    }
}

enum class Color {
    GrayBackground,//灰色背景
    WhiteBackground,//白色背景
    BlackText,//黑色文字
    GrayText//默认色文字
}