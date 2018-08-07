package com.weilylab.xhuschedule.newPackage.ui.fragment.settings

import android.os.Bundle
import android.preference.Preference
import androidx.annotation.XmlRes
import android.preference.PreferenceFragment
import androidx.annotation.StringRes

abstract class BasePreferenceFragment(@XmlRes private val preferencesResId: Int) : PreferenceFragment() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(preferencesResId)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		initPreference()
		monitor()
	}

	open fun initPreference() {}

	open fun monitor() {}

	fun findPreferenceById(@StringRes id: Int): Preference = findPreference(getString(id))
}