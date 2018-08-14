package com.weilylab.xhuschedule.utils

import android.content.Context
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant

object ConfigurationUtil {
	private val sharedPreferences = APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_CONFIG, Context.MODE_PRIVATE)

	var firstEnter: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_CONFIG_FIRST_ENTER, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_CONFIG_FIRST_ENTER, true)
	var isEnableMultiUserMode: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_ENABLE_MULTI_USER_MODE, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_ENABLE_MULTI_USER_MODE, false)
	var isShowNotWeek: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_SHOW_NOT_WEEK, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_NOT_WEEK, false)
	var autoCheckUpdate: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_AUTO_CHECK_UPDATE, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_AUTO_CHECK_UPDATE, true)
	var customUserImage: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_CUSTOM_USER_IMAGE, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_CUSTOM_USER_IMAGE, "")!!
	var customBackgroundImage: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_CUSTOM_BACKGROUND_IMAGE, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_CUSTOM_BACKGROUND_IMAGE, "")!!
	var clearAppData: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean("clearAppData", value)
				.apply()
		get() = sharedPreferences.getBoolean("clearAppData", true)
}