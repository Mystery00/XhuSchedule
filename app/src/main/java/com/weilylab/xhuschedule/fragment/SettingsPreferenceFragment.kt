package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
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
			val datePickerLayout = View.inflate(activity, R.layout.layout_date_picker, null)
			val datePicker: DatePicker = datePickerLayout.findViewById(R.id.datePicker)
			datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)
			val dialog = AlertDialog.Builder(activity)
					.setTitle("请选择时间")
					.setView(datePickerLayout)
					.setPositiveButton(android.R.string.ok, null)
					.setNegativeButton(android.R.string.cancel, null)
					.create()
			dialog.show()
			if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null)
			{
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
					val year = datePicker.year
					val month = datePicker.month + 1
					val dayOfMonth = datePicker.dayOfMonth
					Logs.i(TAG, "monitor: " + year)
					Logs.i(TAG, "monitor: " + month)
					Logs.i(TAG, "monitor: " + dayOfMonth)
					calendar.set(year, month, dayOfMonth, 0, 0, 0)
					if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
					{
						Logs.i(TAG, "monitor: 选择的时间不是周一")
					}
					else
						dialog.dismiss()
				}
			}
			true
		}
	}
}