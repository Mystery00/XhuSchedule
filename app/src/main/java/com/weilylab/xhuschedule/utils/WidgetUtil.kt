package com.weilylab.xhuschedule.utils

import android.content.Context
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant
import java.util.HashSet

object WidgetUtil {
	fun saveWidgetIDs(context: Context, name: String, appWidgetIds: IntArray) {
		val sharedPreferences = context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_IDS, Context.MODE_PRIVATE)
		sharedPreferences.edit().putStringSet(name, appWidgetIds.map { it.toString() }.toSet()).apply()
	}

	fun getWidgetIDs(context: Context, name: String): IntArray {
		val sharedPreferences = context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_IDS, Context.MODE_PRIVATE)
		return sharedPreferences.getStringSet(name, HashSet<String>())!!.map { it.toInt() }.toIntArray()
	}
}