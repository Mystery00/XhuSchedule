/*
 * Created by Mystery0 on 18-2-2 下午10:16.
 * Copyright (c) 2018. All Rights reserved.
 *
 * Last modified 18-2-2 下午10:16
 */

package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R

class DebugFragment:PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_debug)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}