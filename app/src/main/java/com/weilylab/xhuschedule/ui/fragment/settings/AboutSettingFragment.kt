package com.weilylab.xhuschedule.ui.fragment.settings

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.utils.ConfigUtil

class AboutSettingFragment : XhuBasePreferenceFragment(R.xml.preference_about) {
	private val updateLogPreference by lazy { findPreferenceById<Preference>(R.string.key_update_log) }
	private val openSourceLicenseAboutPreference by lazy { findPreferenceById<Preference>(R.string.key_open_source_license_about) }

	override fun monitor() {
		super.monitor()
		updateLogPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			ConfigUtil.showUpdateLog(requireActivity())
			true
		}
		openSourceLicenseAboutPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			val isNight = ContextCompat.getColor(requireActivity(), R.color.isNight) == Color.parseColor("#000000")
			LibsBuilder()
					.withActivityStyle(if (isNight) Libs.ActivityStyle.DARK else Libs.ActivityStyle.LIGHT)
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