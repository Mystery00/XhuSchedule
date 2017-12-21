/*
 * Created by Mystery0 on 17-12-22 上午12:18.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-22 上午12:18
 */

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.ScheduleHelper
import java.util.*

class SnowFragment : PreferenceFragment() {
    private var clickTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_snow)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val key = findPreference("key")
        val category = findPreference("category") as PreferenceCategory
        val letItSnow = findPreference("let_it_snow")
        val merryChristmas = findPreference("merry_christmas")
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.MONTH) != 11 || calendar.get(Calendar.DAY_OF_MONTH) != 25)
            category.removeAll()
        key.setOnPreferenceClickListener {
            if (clickTime >= 7) {
                Toast.makeText(activity, "Merry Christmas ~", Toast.LENGTH_SHORT)
                        .show()
                ScheduleHelper.isShowChristmas = true
                category.removeAll()
                category.addPreference(letItSnow)
                category.addPreference(merryChristmas)
            } else
                clickTime++
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}