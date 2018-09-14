package com.weilylab.xhuschedule.base

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.annotation.XmlRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import com.weilylab.xhuschedule.R

abstract class BasePreferenceFragment(@XmlRes private val preferencesResId: Int) : PreferenceFragmentCompat() {
	private var toast: Toast? = null

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(preferencesResId, rootKey)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = super.onCreateView(inflater, container, savedInstanceState) ?: return null
		listView.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorWhiteBackground))
		setDividerHeight(1)
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

	fun toastMessage(@StringRes stringRes: Int, isShowLong: Boolean = false) = toastMessage(getString(stringRes), isShowLong)

	fun toastMessage(message: String?, isShowLong: Boolean = false) {
		toast?.cancel()
		toast = Toast.makeText(activity!!, message, if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
		toast?.show()
	}
}