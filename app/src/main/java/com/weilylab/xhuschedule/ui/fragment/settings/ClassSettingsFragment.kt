package com.weilylab.xhuschedule.ui.fragment.settings

import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.appcompat.app.AlertDialog
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.BasePreferenceFragment
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.utils.ConfigUtil
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import vip.mystery0.logs.Logs
import vip.mystery0.rxpackagedata.Status.*

class ClassSettingsFragment : BasePreferenceFragment(R.xml.preference_class) {
	private val showNotWeekPreference: CheckBoxPreference by lazy { findPreferenceById(R.string.key_show_not_week) as CheckBoxPreference }
	private val currentYearAndTermPreference: Preference by lazy { findPreferenceById(R.string.key_current_year_and_term) }
	private val customStartTimePreference: Preference by lazy { findPreferenceById(R.string.key_custom_start_time) }

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
				}
			}
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