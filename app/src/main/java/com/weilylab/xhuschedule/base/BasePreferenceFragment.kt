package com.weilylab.xhuschedule.base

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
import com.google.android.material.snackbar.Snackbar
import com.weilylab.xhuschedule.R

abstract class BasePreferenceFragment(@XmlRes private val preferencesResId: Int) : PreferenceFragmentCompat() {
	private var toast: Toast? = null
	private var snackbar: Snackbar? = null

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

	fun snackBarMessage(@StringRes stringRes: Int, isShowLong: Boolean = false) = snackBarMessage(getString(stringRes), isShowLong)

	fun snackBarMessage(message: String, isShowLong: Boolean = false) = snackBarMessage(message, {}, isShowLong)

	fun snackBarMessage(@StringRes stringId: Int, doOther: (Snackbar) -> Unit, showLong: Boolean = false) = snackBarMessage(getString(stringId), doOther, showLong = showLong)

	fun snackBarMessage(message: String, doOther: (Snackbar) -> Unit, showLong: Boolean = false) {
		snackbar?.dismiss()
		snackbar = Snackbar.make(activity!!.findViewById(R.id.coordinatorLayout), message, if (showLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
		doOther.invoke(snackbar!!)
		snackbar!!.show()
	}
}