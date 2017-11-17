package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.APP

/**
 * Created by myste.
 */
object Settings
{
	private val sharedPreference = APP.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

	var firstWeekOfTerm: String
		set(value)
		{
			sharedPreference.edit().putString("firstWeekOfTerm", value).apply()
		}
		get() = sharedPreference.getString("firstWeekOfTerm", "2017-8-4")
}