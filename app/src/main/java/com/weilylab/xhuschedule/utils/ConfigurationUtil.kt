package com.weilylab.xhuschedule.utils

import android.content.Context
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.constant.SharedPreferenceConstant


object ConfigurationUtil {
	private val sharedPreferences by lazy { APP.context.getSharedPreferences(SharedPreferenceConstant.FILE_NAME_CONFIG, Context.MODE_PRIVATE) }

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
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_NOT_WEEK, true)
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
	var nightMode: Int
		set(value) = sharedPreferences.edit()
				.putInt(SharedPreferenceConstant.FIELD_NIGHT_MODE, value)
				.apply()
		get() = sharedPreferences.getInt(SharedPreferenceConstant.FIELD_NIGHT_MODE, 2)
	var isShowGpa: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_SHOW_GPA, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_GPA, false)
	var isShowCredit: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_SHOW_CREDIT, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_CREDIT, false)
	var isShowCourseType: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_SHOW_COURSE_TYPE, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_COURSE_TYPE, false)
	var isShowFailed: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_SHOW_FAILED, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_FAILED, true)
	var deviceID: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_DEVICE_ID, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_DEVICE_ID, "")!!
	var updatedVersion: Int
		set(value) = sharedPreferences.edit()
				.putInt(SharedPreferenceConstant.FIELD_UPDATED_VERSION, value)
				.apply()
		get() = sharedPreferences.getInt(SharedPreferenceConstant.FIELD_UPDATED_VERSION, 0)
	var notificationCourse: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_NOTIFICATION_COURSE, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_NOTIFICATION_COURSE, true)
	var notificationExam: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_NOTIFICATION_EXAM, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_NOTIFICATION_EXAM, true)
	var notificationTime: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_NOTIFICATION_TIME, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_NOTIFICATION_TIME, "20:00")!!
	var ignoreUpdateVersion: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_IGNORE_UPDATE_VERSION, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_IGNORE_UPDATE_VERSION, "")!!
	var startTime: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_START_DATE_TIME, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_START_DATE_TIME, "")!!
	var customStartTime: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_CUSTOM_START_DATE_TIME, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_CUSTOM_START_DATE_TIME, "")!!
	var isCustomStartTime: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_IS_CUSTOM_START_DATE_TIME, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_IS_CUSTOM_START_DATE_TIME, false)
	var currentYear: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_CURRENT_YEAR, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_CURRENT_YEAR, "")!!
	var currentTerm: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_CURRENT_TERM, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_CURRENT_TERM, "")!!
	var isCustomYearAndTerm: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_CUSTOM_YEAR_AND_TERM, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_CUSTOM_YEAR_AND_TERM, false)
	var lastUpdateDate: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_LAST_UPDATE_DATE, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_LAST_UPDATE_DATE, "")!!
	var jrscToken: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_JRSC_TOKEN, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_JRSC_TOKEN, "")!!
	var enableViewPagerTransform: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_ENABLE_VIEW_PAGER_TRANSFORM, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_ENABLE_VIEW_PAGER_TRANSFORM, false)
	var disableJRSC: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_DISABLE_JRSC, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_DISABLE_JRSC, false)
	var showJRSCTranslation: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_SHOW_JRSC_TRANSLATION, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_JRSC_TRANSLATION, false)
	var tintNavigationBar: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_TINT_NAVIGATION_BAR, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_TINT_NAVIGATION_BAR, false)
	var showCustomThingFirst: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_SHOW_CUSTOM_THING_FIRST, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_SHOW_CUSTOM_THING_FIRST, false)
	var showTomorrowCourseAfterTime: String
		set(value) = sharedPreferences.edit()
				.putString(SharedPreferenceConstant.FIELD_SHOW_TOMORROW_COURSE_AFTER, value)
				.apply()
		get() = sharedPreferences.getString(SharedPreferenceConstant.FIELD_SHOW_TOMORROW_COURSE_AFTER, "disable")!!
	var useInAppImageSelector: Boolean
		set(value) = sharedPreferences.edit()
				.putBoolean(SharedPreferenceConstant.FIELD_USE_IN_APP_IMAGE_SELECTOR, value)
				.apply()
		get() = sharedPreferences.getBoolean(SharedPreferenceConstant.FIELD_USE_IN_APP_IMAGE_SELECTOR, false)
}