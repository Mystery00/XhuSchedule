/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.fragment.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.aboutlibraries.LibsBuilder
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.repository.DebugDataKeeper
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import org.koin.android.ext.android.inject
import vip.mystery0.tools.factory.toJson
import vip.mystery0.tools.utils.fastClick


class AboutSettingFragment : XhuBasePreferenceFragment(R.xml.preference_about) {
	private val clipboardManager: ClipboardManager by inject()
	private val shortcutManager: ShortcutManager by inject()
	private val debugDataKeeper: DebugDataKeeper by inject()

	private val updateLogPreference by lazy { findPreferenceById<Preference>(R.string.key_update_log) }
	private val versionNamePreference by lazy { findPreferenceById<Preference>(R.string.key_version_name) }
	private val versionCodePreference by lazy { findPreferenceById<Preference>(R.string.key_version_code) }
	private val openSourceLicenseAboutPreference by lazy { findPreferenceById<Preference>(R.string.key_open_source_license_about) }

	override fun monitor() {
		super.monitor()
		updateLogPreference.setOnPreferenceClickListener {
			ConfigUtil.showUpdateLog(requireActivity())
			true
		}
		versionNamePreference.setOnPreferenceClickListener {
			fastClick(3) {
				toast(resources.getStringArray(R.array.version_name).random())
			}
			true
		}
		versionCodePreference.setOnPreferenceClickListener {
			fastClick(5) {
				val shortcuts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
					shortcutManager.dynamicShortcuts.map { "${it.id}-${it.shortLabel}" }.toJson()
				} else {
					""
				}
				val message = """
					deviceId: ${ConfigUtil.getDeviceID()},
					startTime: ${ConfigurationUtil.startTime},
					customStartTime: ${ConfigurationUtil.customStartTime},
					currentYear: ${ConfigurationUtil.currentYear},
					currentTerm: ${ConfigurationUtil.currentTerm},
					lastUpdateDate: ${ConfigurationUtil.lastUpdateDate},
					android: ${Build.VERSION.RELEASE}_${Build.VERSION.SDK_INT},
					vendor: ${Build.MANUFACTURER},
					model: ${Build.MODEL},
					shortcuts: $shortcuts
					=========
					${debugDataKeeper.data.map { "${it.key}: ${it.value.toJson()}" }.sorted().joinToString()}
				""".trimIndent()
				MaterialAlertDialogBuilder(requireActivity())
						.setTitle("debug")
						.setMessage(message)
						.setPositiveButton(android.R.string.copy) { _, _ ->
							clipboardManager.setPrimaryClip(ClipData.newPlainText("XhuSchedule-Debug-Data", message))
						}
						.setPositiveButton(android.R.string.ok, null)
						.show()
			}
			true
		}
		openSourceLicenseAboutPreference.setOnPreferenceClickListener {
			LibsBuilder()
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
					.start(requireActivity())
			true
		}
	}
}