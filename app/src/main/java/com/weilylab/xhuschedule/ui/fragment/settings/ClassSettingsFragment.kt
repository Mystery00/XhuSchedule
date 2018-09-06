package com.weilylab.xhuschedule.ui.fragment.settings

import android.preference.CheckBoxPreference
import android.preference.Preference
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.utils.CalendarUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import java.util.*

class ClassSettingsFragment : BasePreferenceFragment(R.xml.preference_class) {
	private lateinit var showNotWeekPreference: CheckBoxPreference
	private lateinit var customStartTimePreference: Preference

	override fun initPreference() {
		super.initPreference()
		showNotWeekPreference = findPreferenceById(R.string.key_show_not_week) as CheckBoxPreference
		customStartTimePreference = findPreferenceById(R.string.key_custom_start_time)

		showNotWeekPreference.isChecked = ConfigurationUtil.isShowNotWeek
		updateCustomStartTimeSummary()
	}

	override fun monitor() {
		super.monitor()
		showNotWeekPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.isShowNotWeek = !showNotWeekPreference.isChecked
			LayoutRefreshConfigUtil.isRefreshTableFragment = true
			true
		}
		customStartTimePreference.setOnPreferenceClickListener {
			val datePicker = DatePicker(activity)
			val startTime = InitLocalDataSource.getStartDateTime()
			datePicker.init(startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH)) { _, year, monthOfYear, dayOfMonth ->
				startTime.set(year, monthOfYear, dayOfMonth)
			}
			AlertDialog.Builder(activity!!)
					.setView(datePicker)
					.setPositiveButton(R.string.action_ok) { _, _ ->
						CalendarUtil.setCustomStartTime(startTime)
						updateCustomStartTimeSummary()
						LayoutRefreshConfigUtil.isRefreshStartTime = true
						LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
					}
					.setNegativeButton(R.string.action_cancel, null)
					.setNeutralButton(R.string.action_default) { _, _ ->
						CalendarUtil.setCustomStartTime(null)
						updateCustomStartTimeSummary()
						LayoutRefreshConfigUtil.isRefreshStartTime = true
						LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
					}
					.show()
			true
		}
	}

	private fun updateCustomStartTimeSummary() {
		val summary = if (ConfigurationUtil.isCustomStartTime)
			ConfigurationUtil.customStartTime
		else
			ConfigurationUtil.startTime
		customStartTimePreference.summary = getString(R.string.summaru_custom_start_time, summary)
	}
}