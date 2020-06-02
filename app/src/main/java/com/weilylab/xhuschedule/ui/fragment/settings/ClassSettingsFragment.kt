/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.fragment.settings

import android.app.Dialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import cc.shinichi.library.ImagePreview
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewmodel.SettingsViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ClassSettingsFragment : XhuBasePreferenceFragment(R.xml.preference_class) {
	private val eventBus: EventBus by inject()

	private val settingsViewModel: SettingsViewModel by viewModel()

	private val showNotWeekPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_show_not_week) }
	private val currentYearAndTermPreference by lazy { findPreferenceById<Preference>(R.string.key_current_year_and_term) }
	private val customStartTimePreference by lazy { findPreferenceById<Preference>(R.string.key_custom_start_time) }
	private val showTomorrowCourseAfterPreference by lazy { findPreferenceById<Preference>(R.string.key_show_tomorrow_course_after) }
	private val schoolCalendarPreference by lazy { findPreferenceById<Preference>(R.string.key_action_school_calendar) }
	private val showCustomThingFirstPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_show_custom_thing_first) }

	private val dialog: Dialog by lazy {
		ZLoadingDialog(requireActivity())
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_school_calendar))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
				.create()
	}
	private val downloadButton by lazy {
		val instance = FloatingActionButton(requireActivity())
		instance.setImageResource(R.drawable.ic_school_calendar_download)
		instance
	}
	private var schoolCalendarUrl: String? = null

	override fun initPreference() {
		super.initPreference()
		settingsViewModel.initStudentList()
		showNotWeekPreference.isChecked = ConfigurationUtil.isShowNotWeek
		currentYearAndTermPreference.summary = getString(R.string.summary_current_year_and_term, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm)
		updateCustomStartTimeSummary()
		if (ConfigurationUtil.showTomorrowCourseAfterTime != "disable")
			showTomorrowCourseAfterPreference.summary = getString(R.string.summary_show_tomorrow_after_time, ConfigurationUtil.showTomorrowCourseAfterTime)
		else
			showTomorrowCourseAfterPreference.summary = getString(R.string.summary_show_tomorrow_after_time_disable)
	}

	override fun monitor() {
		super.monitor()
		showNotWeekPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.isShowNotWeek = !showNotWeekPreference.isChecked
			eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
			true
		}
		currentYearAndTermPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			settingsViewModel.queryAllStudentInfoListAndThen { list ->
				val sortList = list.sortedBy { it.grade.toInt() }
				val oldGrade = sortList.first().grade.toInt()
				val newGrade = sortList.last().grade.toInt()
				val tempArray = Array(2 * (newGrade - oldGrade + 4)) { i -> "${oldGrade + i / 2}-${oldGrade + i / 2 + 1}学年 第${i % 2 + 1}学期" }.sortedArrayDescending()
				val tempArrayList = ArrayList<String>()
				tempArrayList.addAll(tempArray)
				tempArrayList.add(0, "自动获取")
				val selectArray = tempArrayList.toTypedArray()
				val currentString = "${ConfigurationUtil.currentYear}学年 第${ConfigurationUtil.currentTerm}学期"
				var selectedIndex = if (ConfigurationUtil.isCustomYearAndTerm) selectArray.indexOf(currentString) else 0
				AlertDialog.Builder(requireActivity())
						.setTitle(R.string.title_current_year_and_term)
						.setSingleChoiceItems(selectArray, selectedIndex) { _, checkItem ->
							selectedIndex = checkItem
						}
						.setPositiveButton(R.string.action_ok) { _, _ ->
							if (selectedIndex == 0) {
								ConfigurationUtil.isCustomYearAndTerm = false
								settingsViewModel.updateCurrentYearAndTerm()
							} else {
								ConfigurationUtil.isCustomYearAndTerm = true
								val select = selectArray[selectedIndex]
								val year = select.substring(0, 9)
								val term = select.substring(13, 14)
								ConfigurationUtil.currentYear = year
								ConfigurationUtil.currentTerm = term
							}
							currentYearAndTermPreference.summary = getString(R.string.summary_current_year_and_term, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm)
							eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
						}
						.setNegativeButton(R.string.action_cancel, null)
						.show()
			}
			true
		}
		showTomorrowCourseAfterPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			val time = ConfigurationUtil.showTomorrowCourseAfterTime
			val oldHour: Int
			val oldMinute: Int
			if (time == "disable") {
				val calendar = Calendar.getInstance()
				oldHour = calendar.get(Calendar.HOUR_OF_DAY)
				oldMinute = calendar.get(Calendar.MINUTE)
			} else {
				val array = time.split(':')
				oldHour = array[0].toInt()
				oldMinute = array[1].toInt()
			}
			val timePickerDialog = TimePickerDialog(requireActivity(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
				val hourString = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
				val minuteString = if (minute < 10) "0$minute" else minute.toString()
				val newString = "$hourString:$minuteString"
				ConfigurationUtil.showTomorrowCourseAfterTime = newString
				showTomorrowCourseAfterPreference.summary = getString(R.string.summary_show_tomorrow_after_time, ConfigurationUtil.showTomorrowCourseAfterTime)
				eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT, UI.MENU)))
			}, oldHour, oldMinute, true)
			timePickerDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.action_disable)) { _, _ ->
				ConfigurationUtil.showTomorrowCourseAfterTime = "disable"
				showTomorrowCourseAfterPreference.summary = getString(R.string.summary_show_tomorrow_after_time_disable)
				eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT, UI.MENU)))
			}
			timePickerDialog.show()
			true
		}
		schoolCalendarPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			fun jumpToImageView(url: String) {
				ImagePreview.getInstance()
						.setContext(requireActivity())
						.setImage(url)
//						.setCustomDownButtonView(downloadButton)
						.setShowIndicator(false)
//						.setPickDirectoryWhenDownloadImage(true)
						.start()
			}
			if (schoolCalendarUrl == null) {
				dialog.show()
				settingsViewModel.getSchoolCalendarUrl { url ->
					if (url != null) {
						schoolCalendarUrl = url
						jumpToImageView(schoolCalendarUrl!!)
					} else
						toast(R.string.hint_school_calendar)
					dialog.dismiss()
				}
			} else {
				jumpToImageView(schoolCalendarUrl!!)
			}
			true
		}
		showCustomThingFirstPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.showCustomThingFirst = !showCustomThingFirstPreference.isChecked
			eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
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
		customStartTimePreference.summary = getString(R.string.summary_custom_start_time, summary)
	}
}