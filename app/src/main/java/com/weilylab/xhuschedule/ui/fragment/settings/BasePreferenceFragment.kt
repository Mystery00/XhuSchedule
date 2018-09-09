package com.weilylab.xhuschedule.ui.fragment.settings

import android.graphics.Color
import android.os.Bundle
import android.preference.Preference
import androidx.annotation.XmlRes
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.annotation.StringRes

abstract class BasePreferenceFragment(@XmlRes private val preferencesResId: Int) : PreferenceFragment() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(preferencesResId)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = super.onCreateView(inflater, container, savedInstanceState) ?: return null
		val list = view.findViewById<ListView>(android.R.id.list)
		list.setBackgroundColor(Color.WHITE)
		return view
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