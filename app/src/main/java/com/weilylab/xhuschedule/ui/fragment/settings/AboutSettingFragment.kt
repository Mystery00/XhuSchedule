package com.weilylab.xhuschedule.ui.fragment.settings

import androidx.preference.Preference
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.BasePreferenceFragment
import com.weilylab.xhuschedule.utils.ConfigUtil

class AboutSettingFragment : BasePreferenceFragment(R.xml.preference_about) {
	private lateinit var updateLogPreference: Preference
	private lateinit var openSourceLicenseAboutPreference: Preference

	override fun initPreference() {
		super.initPreference()
		updateLogPreference = findPreferenceById(R.string.key_update_log)
		openSourceLicenseAboutPreference = findPreferenceById(R.string.key_open_source_license_about)
	}

	override fun monitor() {
		super.monitor()
		updateLogPreference.setOnPreferenceClickListener {
			ConfigUtil.showUpdateLog(activity!!)
			true
		}
		openSourceLicenseAboutPreference.setOnPreferenceClickListener {
			LibsBuilder()
					.withActivityStyle(Libs.ActivityStyle.LIGHT)
					.withAboutAppName(getString(R.string.app_name))
					.withAboutIconShown(true)
					.withAboutVersionShown(true)
					.withLicenseShown(true)
					.withLicenseDialog(true)
					.withShowLoadingProgress(true)
					.withLibraries(
							"BottomTabView",
							"ColorPicker",
							"Condom",
							"CosmoCalendar",
							"DataBinding",
							"Lifecycles",
							"Matisse",
							"Mystery0Tools",
							"Room",
							"TimetableView",
							"uCrop",
							"ViewModel",
							"ZLoading")
					.start(activity!!)
			true
		}
	}
}