/*
 * Created by Mystery0 on 17-12-22 上午12:18.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-22 上午12:18
 */

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.TempSharedPreferenceUtil

class SnowFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_snow)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val enable = findPreference("enable") as SwitchPreference

        enable.isChecked = TempSharedPreferenceUtil.snowFall

        enable.setOnPreferenceChangeListener { _, _ ->
            val snowFall = !enable.isChecked
            TempSharedPreferenceUtil.snowFall = snowFall
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}