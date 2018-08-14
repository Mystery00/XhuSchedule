package com.weilylab.xhuschedule.ui.fragment.settings

import android.preference.CheckBoxPreference
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil

class ClassSettingsFragment : BasePreferenceFragment(R.xml.preference_class) {
	private lateinit var showNotWeekPreference: CheckBoxPreference

	override fun initPreference() {
		super.initPreference()
		showNotWeekPreference = findPreferenceById(R.string.key_show_not_week) as CheckBoxPreference

		showNotWeekPreference.isChecked = ConfigurationUtil.isShowNotWeek
	}

	override fun monitor() {
		super.monitor()
		showNotWeekPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.isShowNotWeek = !showNotWeekPreference.isChecked
			LayoutRefreshConfigUtil.isRefreshTableFragment = true
			true
		}
	}
}