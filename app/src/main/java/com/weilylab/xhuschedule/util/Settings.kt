/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-26 下午4:44
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.APP

/**
 * Created by myste.
 */
object Settings {
    private val sharedPreference = APP.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

    var firstWeekOfTerm: String
        set(value) = sharedPreference.edit().putString("firstWeekOfTerm", value).apply()
        get() = sharedPreference.getString("firstWeekOfTerm", "2017-8-4")
    var isShowNot: Boolean
        set(value) = sharedPreference.edit().putBoolean("isShowNot", value).apply()
        get() = sharedPreference.getBoolean("isShowNot", true)
    var isFirstRun: Boolean
        set(value) = sharedPreference.edit().putBoolean("isFirstRun", value).apply()
        get() = sharedPreference.getBoolean("isFirstRun", true)
    var isNeedClear: Boolean
        set(value) = sharedPreference.edit().putBoolean("isNeedClear", value).apply()
        get() = sharedPreference.getBoolean("isNeedClear", true)
    var nickName: String
        set(value) = sharedPreference.edit().putString("nickName", value).apply()
        get() = sharedPreference.getString("nickName", "")
    var userImg: String
        set(value) = sharedPreference.edit().putString("userImg", value).apply()
        get() = sharedPreference.getString("userImg", "")
    var customHeaderImg: String
        set(value) = sharedPreference.edit().putString("customHeaderImg", value).apply()
        get() = sharedPreference.getString("customHeaderImg", "")
    var customBackgroundImg: String
        set(value) = sharedPreference.edit().putString("customBackgroundImg", value).apply()
        get() = sharedPreference.getString("customBackgroundImg", "")
    var customTableOpacity: Int
        set(value) = sharedPreference.edit().putInt("customTableOpacity", value).apply()
        get() = sharedPreference.getInt("customTableOpacity", 154)
    var customTodayOpacity: Int
        set(value) = sharedPreference.edit().putInt("customTodayOpacity", value).apply()
        get() = sharedPreference.getInt("customTodayOpacity", 154)
    var customTableTextColor: Int
        set(value) = sharedPreference.edit().putInt("customTableTextColor", value).apply()
        get() = sharedPreference.getInt("customTableTextColor", -1)
    var customTodayTextColor: Int
        set(value) = sharedPreference.edit().putInt("customTodayTextColor", value).apply()
        get() = sharedPreference.getInt("customTodayTextColor", -11184811)
    var customTextSize: Int
        set(value) = sharedPreference.edit().putInt("customTextSize", value).apply()
        get() = sharedPreference.getInt("customTextSize", 12)
    var customTextHeight: Int
        set(value) = sharedPreference.edit().putInt("customHeightSize", value).apply()
        get() = sharedPreference.getInt("customHeightSize", 144)
}