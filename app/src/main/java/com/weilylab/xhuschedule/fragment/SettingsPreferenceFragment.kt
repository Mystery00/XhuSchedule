package com.weilylab.xhuschedule.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import vip.mystery0.tools.logs.Logs
import java.util.*

/**
 * Created by myste.
 */
class SettingsPreferenceFragment : PreferenceFragment()
{
	companion object
	{
		private val TAG = "SettingsPreferenceFragment"
	}

	private lateinit var firstDayPreference: Preference

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(R.xml.preferences)
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
							  savedInstanceState: Bundle?): View
	{
		initialization()
		monitor()
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	private fun initialization()
	{
		firstDayPreference = findPreference(getString(R.string.key_first_day))
	}

	private fun monitor()
	{
		firstDayPreference.setOnPreferenceClickListener {
			val calendar = Calendar.getInstance(Locale.CHINA)
			DatePickerDialog(activity,
					DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
						Logs.i(TAG, "monitor: " + year)
						Logs.i(TAG, "monitor: " + month)
						Logs.i(TAG, "monitor: " + dayOfMonth)
						calendar.set(year, month, dayOfMonth, 0, 0, 0)
						if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
						{
							Logs.i(TAG, "monitor: 选择的时间不是周一")
						}
					}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
					.show()
			true
		}
	}
}