package com.weilylab.xhuschedule.ui.fragment.settings

import android.preference.CheckBoxPreference
import android.preference.Preference
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil

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
	}

	override fun onResume() {
		super.onResume()
		updateCustomStartTimeSummary()
	}

	private fun updateCustomStartTimeSummary() {
		val summary = if (ConfigurationUtil.isCustomStartTime)
			ConfigurationUtil.customStartTime
		else
			ConfigurationUtil.startTime
		customStartTimePreference.summary = getString(R.string.summaru_custom_start_time, summary)
	}
}