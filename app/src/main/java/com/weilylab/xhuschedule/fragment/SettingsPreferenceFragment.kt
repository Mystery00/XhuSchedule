package com.weilylab.xhuschedule.fragment

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.view.CustomDatePicker
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

		val date = Settings.firstWeekOfTerm.split('-')
		firstDayPreference.summary = date[0] + '-' + (date[1].toInt() + 1) + '-' + date[2]
	}

	private fun monitor()
	{
		firstDayPreference.setOnPreferenceClickListener {
			val calendar = Calendar.getInstance(Locale.CHINA)
			val firstWeekOfTerm = Settings.firstWeekOfTerm
			val date = firstWeekOfTerm.split('-')
			calendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
			val datePicker = CustomDatePicker(activity)
			datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)
			val dialog = AlertDialog.Builder(activity)
					.setTitle(R.string.title_dialog_time)
					.setView(datePicker)
					.setPositiveButton(android.R.string.ok, null)
					.setNegativeButton(android.R.string.cancel, null)
					.create()
			dialog.show()
			if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null)
			{
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
					calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, 0, 0, 0)
					when
					{
						calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY -> Snackbar.make(datePicker, R.string.error_time_format, Snackbar.LENGTH_SHORT)
								.show()
						calendar.after(Calendar.getInstance()) -> Snackbar.make(datePicker, R.string.error_time_after, Snackbar.LENGTH_SHORT)
								.show()
						else ->
						{
							Settings.firstWeekOfTerm = datePicker.year.toString() + '-' + datePicker.month.toString() + '-' + datePicker.dayOfMonth.toString()
							firstDayPreference.summary = datePicker.year.toString() + '-' + (datePicker.month + 1).toString() + '-' + datePicker.dayOfMonth.toString()
							dialog.dismiss()
						}
					}
				}
			}
			true
		}
	}
}