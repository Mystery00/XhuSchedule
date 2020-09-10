/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.ui.fragment.settings

import android.Manifest
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import cc.shinichi.library.ImagePreview
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.databinding.DialogSetReminderBinding
import com.weilylab.xhuschedule.databinding.ItemReminderBinding
import com.weilylab.xhuschedule.databinding.LayoutExportToCalendarBinding
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewmodel.SettingsViewModel
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.rx.DataObserver
import java.util.*
import kotlin.collections.ArrayList

class ClassSettingsFragment : XhuBasePreferenceFragment(R.xml.preference_class) {
	private val eventBus: EventBus by inject()

	private val settingsViewModel: SettingsViewModel by viewModel()

	private val showNotWeekPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_show_not_week) }
	private val currentYearAndTermPreference by lazy { findPreferenceById<Preference>(R.string.key_current_year_and_term) }
	private val customStartTimePreference by lazy { findPreferenceById<Preference>(R.string.key_custom_start_time) }
	private val showTomorrowCourseAfterPreference by lazy { findPreferenceById<Preference>(R.string.key_show_tomorrow_course_after) }
	private val schoolCalendarPreference by lazy { findPreferenceById<Preference>(R.string.key_action_school_calendar) }
	private val toCalendarPreference by lazy { findPreferenceById<Preference>(R.string.key_action_export_to_calendar) }
	private val showCustomThingFirstPreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_show_custom_thing_first) }

	private val exportToCalendarBinding by lazy { LayoutExportToCalendarBinding.inflate(LayoutInflater.from(requireContext())) }
	private val dialogSetReminderBinding by lazy { DialogSetReminderBinding.inflate(LayoutInflater.from(requireContext())) }
	private val bottomSheetDialog by lazy { BottomSheetDialog(requireActivity()) }
	private val setReminderDialog by lazy {
		MaterialAlertDialogBuilder(requireActivity())
				.setView(dialogSetReminderBinding.root)
				.setPositiveButton(android.R.string.ok) { dialog, _ ->
					val remindTime = when (dialogSetReminderBinding.radioGroup.checkedRadioButtonId) {
						R.id.radioButton5 -> 5
						R.id.radioButton10 -> 10
						R.id.radioButtonCustom -> {
							val input = dialogSetReminderBinding.editTextNumber.text.toString()
							if (input.isBlank()) {
								toastLong(R.string.error_set_reminder_empty_input)
								return@setPositiveButton
							}
							input.toInt()
						}
						else -> 0
					}
					dialog.dismiss()
					handleAddRemindLayout(remindTime)
				}
				.setNegativeButton(android.R.string.cancel, null)
				.create()
	}

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
	private val exportDialog: Dialog by lazy {
		ZLoadingDialog(requireActivity())
				.setLoadingBuilder(Z_TYPE.STAR_LOADING)
				.setHintText(getString(R.string.hint_dialog_export_calendar))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
				.create()
	}
	private var schoolCalendarUrl: String? = null
	private val selectedStudentList = ArrayList<Student>()
	private val remindTimeList = ArrayList<Int>()

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		settingsViewModel.exportCalendar.observe(requireActivity(), object : DataObserver<Boolean> {
			override fun contentNoEmpty(data: Boolean) {
				super.contentNoEmpty(data)
				toast(R.string.hint_export_done)
				exportDialog.dismiss()
			}

			override fun error(e: Throwable?) {
				super.error(e)
				toast(e)
				exportDialog.dismiss()
			}
		})
		initExportLayout()
	}

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

	private fun initExportLayout() {
		bottomSheetDialog.setContentView(exportToCalendarBinding.root)
		bottomSheetDialog.setCancelable(true)
		bottomSheetDialog.setCanceledOnTouchOutside(true)
		exportToCalendarBinding.imageViewClose.setOnClickListener {
			bottomSheetDialog.dismiss()
		}
		exportToCalendarBinding.buttonExport.setOnClickListener {
			requestPermissionsOnFragment(arrayOf(Manifest.permission.WRITE_CALENDAR)) { _, result ->
				if (result.isEmpty() || result[0] == PackageManager.PERMISSION_GRANTED) {
					if (selectedStudentList.isEmpty()) {
						//空的账号列表
						toast(R.string.error_export_calendar_empty_account)
					} else {
						bottomSheetDialog.dismiss()
						exportDialog.show()
						settingsViewModel.exportToCalendar(selectedStudentList, remindTimeList, exportToCalendarBinding.switchExportCustomCourse.isChecked, exportToCalendarBinding.switchExportCustomThing.isChecked)
					}
				}
			}
		}
		exportToCalendarBinding.textViewExportSelectStudent.setOnClickListener {
			settingsViewModel.queryAllStudentListAndThen { list ->
				val selectArray = Array(list.size) { "${list[it].username}(${list[it].studentName})" }
				val selectBooleanArray = BooleanArray(list.size) { selectedStudentList.contains(list[it]) }
				MaterialAlertDialogBuilder(requireActivity())
						.setTitle(R.string.hint_dialog_export_select_student)
						.setMultiChoiceItems(selectArray, selectBooleanArray) { _, i: Int, b: Boolean ->
							selectBooleanArray[i] = b
						}
						.setPositiveButton(android.R.string.ok) { _, _ ->
							selectBooleanArray.forEachIndexed { index, b ->
								val student = list[index]
								if (b) {
									if (!selectedStudentList.contains(student)) {
										selectedStudentList.add(student)
									}
								} else {
									if (selectedStudentList.contains(student)) {
										selectedStudentList.remove(student)
									}
								}
							}
							exportToCalendarBinding.chipGroupSelect.removeAllViews()
							selectedStudentList.forEach { student ->
								val chip = Chip(ContextThemeWrapper(requireActivity(), R.style.Widget_MaterialComponents_Chip_Entry))
								chip.isCloseIconVisible = true
								val text = "${student.username}(${student.studentName})"
								chip.text = text
								chip.setOnCloseIconClickListener { view ->
									selectedStudentList.remove(student)
									exportToCalendarBinding.chipGroupSelect.removeView(view)
								}
								exportToCalendarBinding.chipGroupSelect.addView(chip)
							}
						}
						.setNegativeButton(android.R.string.cancel, null)
						.show()
			}
		}
		exportToCalendarBinding.textViewAddReminder.setOnClickListener {
			setReminderDialog.show()
		}
	}

	override fun monitor() {
		super.monitor()
		showNotWeekPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.isShowNotWeek = !showNotWeekPreference.isChecked
			eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
			true
		}
		currentYearAndTermPreference.setOnPreferenceClickListener {
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
			val timePickerDialog = TimePickerDialog(requireActivity(), { _, hourOfDay, minute ->
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
		schoolCalendarPreference.setOnPreferenceClickListener {
			fun jumpToImageView(url: String) {
				ImagePreview.getInstance()
						.setContext(requireActivity())
						.setImage(url)
						.setFolderName(getString(R.string.app_name))
						.setDownIconResId(R.drawable.ic_file_download_white_24dp)
						.setShowIndicator(false)
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
		toCalendarPreference.setOnPreferenceClickListener {
			bottomSheetDialog.show()
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

	private fun handleAddRemindLayout(remindTime: Int) {
		if (remindTimeList.contains(remindTime))
			return
		if (remindTimeList.isEmpty()) {
			//列表为空，清除占位的图标
			exportToCalendarBinding.imageViewIcon.setImageDrawable(null)
		}
		//创建新的项
		val binding = ItemReminderBinding.inflate(LayoutInflater.from(requireContext()))
		//设置是否展示图标
		if (remindTimeList.isNotEmpty()) {
			binding.imageViewIcon.setImageDrawable(null)
		}
		binding.textViewReminder.text = getString(R.string.hint_dialog_export_reminder_custom, remindTime)
		binding.imageViewDelete.setOnClickListener {
			handleRemoveRemindLayout(binding.root, remindTime)
		}
		exportToCalendarBinding.setRemindLayout.addView(binding.root, remindTimeList.size)
		remindTimeList.add(remindTime)
	}

	private fun handleRemoveRemindLayout(view: View, remindTime: Int) {
		exportToCalendarBinding.setRemindLayout.removeView(view)
		remindTimeList.remove(remindTime)
		exportToCalendarBinding.setRemindLayout[0].findViewById<ImageView>(R.id.imageViewIcon).setImageResource(R.drawable.ic_all_day)
	}
}