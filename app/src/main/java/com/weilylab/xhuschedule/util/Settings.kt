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
    var isFirstRun: Boolean
        set(value) = sharedPreference.edit().putBoolean("isFirstRun", value).apply()
        get() = sharedPreference.getBoolean("isFirstRun", true)
    var customHeaderImg: String
        set(value) = sharedPreference.edit().putString("customHeaderImg", value).apply()
        get() = sharedPreference.getString("customHeaderImg", "")
    var customBackgroundImg: String
        set(value) = sharedPreference.edit().putString("customBackgroundImg", value).apply()
        get() = sharedPreference.getString("customBackgroundImg", "")
    var customTransparency: Int
        set(value) = sharedPreference.edit().putInt("customTransparency", value).apply()
        get() = sharedPreference.getInt("customTransparency", 102)
}