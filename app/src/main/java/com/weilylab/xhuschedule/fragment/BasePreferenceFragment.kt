/*
 * Created by Mystery0 on 18-2-3 上午11:01.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-3 上午11:01
 */

package com.weilylab.xhuschedule.fragment

import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.annotation.StringRes

open class BasePreferenceFragment : PreferenceFragment() {
    fun findPreference(@StringRes id: Int): Preference = findPreference(getString(id))
}