package com.weilylab.xhuschedule.ui.fragment.settings

import android.app.Dialog
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import cc.shinichi.library.ImagePreview
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.repository.SchoolCalendarRepository
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.Status.*

class ClassSettingsFragmentXhu : XhuBasePreferenceFragment(R.xml.preference_class) {
	private val showNotWeekPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_show_not_week) as CheckBoxPreference }
	private val currentYearAndTermPreference: Preference by lazy { findPreferenceById(R.string.key_current_year_and_term) }
	private val customStartTimePreference: Preference by lazy { findPreferenceById(R.string.key_custom_start_time) }
	private val schoolCalendarPreference: Preference by lazy { findPreferenceById(R.string.key_action_school_calendar) }
	private val showCustomThingFirstPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_show_custom_thing_first) as CheckBoxPreference }

	private val dialog: Dialog by lazy {
		ZLoadingDialog(activity!!)
				.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
				.setHintText(getString(R.string.hint_dialog_school_calendar))
				.setHintTextSize(16F)
				.setCanceledOnTouchOutside(false)
				.setDialogBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorWhiteBackground))
				.setLoadingColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
				.setHintTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
				.create()
	}
	private val downloadButton by lazy {
		val instance = FloatingActionButton(activity!!)
		instance.setImageResource(R.drawable.ic_school_calendar_download)
		instance
	}
	private var schoolCalendarUrl: String? = null

	override fun initPreference() {
		super.initPreference()
		showNotWeekPreference.isChecked = ConfigurationUtil.isShowNotWeek
		currentYearAndTermPreference.summary = getString(R.string.summary_current_year_and_term, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm)
		updateCustomStartTimeSummary()
	}

	override fun monitor() {
		super.monitor()
		showNotWeekPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.isShowNotWeek = !showNotWeekPreference.isChecked
			LayoutRefreshConfigUtil.isRefreshTableFragment = true
			true
		}
		currentYearAndTermPreference.setOnPreferenceClickListener {
			StudentLocalDataSource.queryAllStudentInfo { packageData ->
				when (packageData.status) {
					Content -> {
						val studentInfoList = packageData.data!!
						val sortList = studentInfoList.sortedBy { s -> s.grade.toInt() }
						val oldGrade = sortList.first().grade.toInt()
						val newGrade = sortList.last().grade.toInt()
						val tempArray = Array(2 * (newGrade - oldGrade + 4)) { i -> "${oldGrade + i / 2}-${oldGrade + i / 2 + 1}学年 第${i % 2 + 1}学期" }.sortedArrayDescending()
						val tempArrayList = ArrayList<String>()
						tempArrayList.addAll(tempArray)
						tempArrayList.add(0, "自动获取")
						val selectArray = tempArrayList.toTypedArray()
						val currentString = "${ConfigurationUtil.currentYear}学年 第${ConfigurationUtil.currentTerm}学期"
						var selectedIndex = if (ConfigurationUtil.isCustomYearAndTerm) selectArray.indexOf(currentString) else 0
						AlertDialog.Builder(activity!!)
								.setTitle(R.string.title_current_year_and_term)
								.setSingleChoiceItems(selectArray, selectedIndex) { _, checkItem ->
									selectedIndex = checkItem
								}
								.setPositiveButton(R.string.action_ok) { _, _ ->
									if (selectedIndex == 0) {
										ConfigurationUtil.isCustomYearAndTerm = false
										ConfigUtil.getCurrentYearAndTerm()
									} else {
										ConfigurationUtil.isCustomYearAndTerm = true
										val select = selectArray[selectedIndex]
										val year = select.substring(0, 9)
										val term = select.substring(13, 14)
										ConfigurationUtil.currentYear = year
										ConfigurationUtil.currentTerm = term
									}
									currentYearAndTermPreference.summary = getString(R.string.summary_current_year_and_term, ConfigurationUtil.currentYear, ConfigurationUtil.currentTerm)
									LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
								}
								.setNegativeButton(R.string.action_cancel, null)
								.show()
					}
					Empty -> {
						toastMessage(R.string.hint_data_null)
					}
					Error -> {
						Logs.wtfm("monitor: ", packageData.error)
						toastMessage(packageData.error?.message)
					}
					Loading -> {
					}
				}
			}
			true
		}
		schoolCalendarPreference.setOnPreferenceClickListener {
			if (schoolCalendarUrl == null) {
				dialog.show()
				SchoolCalendarRepository.getUrl { url ->
					if (url != null) {
						schoolCalendarUrl = url
						ImagePreview.getInstance()
								.setContext(activity!!)
								.setImage(schoolCalendarUrl!!)
								.setCustomDownButtonView(downloadButton)
								.setFolderName("Pictures")
								.setShowIndicator(false)
								.start()
					} else
						toastMessage(R.string.hint_school_calendar)
					dialog.dismiss()
				}
			} else {
				ImagePreview.getInstance()
						.setContext(activity!!)
						.setImage(schoolCalendarUrl!!)
						.setCustomDownButtonView(downloadButton)
						.setFolderName("Pictures")
						.setShowIndicator(false)
						.start()
			}
			true
		}
		showCustomThingFirstPreference.setOnPreferenceChangeListener { _, _ ->
			ConfigurationUtil.showCustomThingFirst = !showCustomThingFirstPreference.isChecked
			LayoutRefreshConfigUtil.isRefreshTodayFragment = true
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