package com.weilylab.xhuschedule.newPackage.utils

import android.content.Context
import com.weilylab.xhuschedule.newPackage.config.APP
import com.weilylab.xhuschedule.newPackage.constant.SharedPreferenceConstant

object ConfigurationUtil {
	private val sharedPreferences = APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_CONFIG, Context.MODE_PRIVATE)

	var firstEnter: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_CONFIG_FIRST_ENTER, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_CONFIG_FIRST_ENTER, true)
}