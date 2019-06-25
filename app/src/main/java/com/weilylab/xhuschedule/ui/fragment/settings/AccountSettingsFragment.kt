package com.weilylab.xhuschedule.ui.fragment.settings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.base.XhuBasePreferenceFragment
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.ui.activity.LoginActivity
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import vip.mystery0.logs.Logs
import vip.mystery0.rx.OnlyCompleteObserver
import vip.mystery0.rx.Status.Content
import vip.mystery0.rx.Status.Error

class AccountSettingsFragment : XhuBasePreferenceFragment(R.xml.preference_account) {
	companion object {
		const val ADD_ACCOUNT_CODE = 233
	}

	private val loggedStudentCategory by lazy { findPreferenceById<PreferenceCategory>(R.string.key_logged_account) }
	private val addAccountPreference by lazy { findPreferenceById<Preference>(R.string.key_add_account) }
	private val delAccountPreference by lazy { findPreferenceById<Preference>(R.string.key_del_account) }
	private val setMainAccountPreference by lazy { findPreferenceById<Preference>(R.string.key_set_main_account) }
	private val enableMultiUserModePreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_enable_multi_user_mode) }
	private val studentList = ArrayList<Student>()

	override fun initPreference() {
		super.initPreference()
		initStudentList()
		setMainAccountPreference.isEnabled = !ConfigurationUtil.isEnableMultiUserMode
		enableMultiUserModePreference.isChecked = ConfigurationUtil.isEnableMultiUserMode
	}

	override fun monitor() {
		super.monitor()
		addAccountPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			startActivityForResult(Intent(activity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
			true
		}
		delAccountPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
			val valueArray = Array(studentList.size) { i -> "${studentList[i].username}(${studentList[i].studentName})" }
			val checkedArray = BooleanArray(studentList.size) { false }
			AlertDialog.Builder(activity!!)
					.setTitle(R.string.title_del_account)
					.setMultiChoiceItems(valueArray, checkedArray) { _, which, isChecked ->
						checkedArray[which] = isChecked
					}
					.setPositiveButton(android.R.string.ok) { _, _ ->
						val needDeleteStudentList = ArrayList<Student>()
						checkedArray.forEachIndexed { index, bool ->
							if (bool) needDeleteStudentList.add(studentList[index])
						}
						StudentLocalDataSource.deleteStudent(needDeleteStudentList, object : OnlyCompleteObserver<Boolean>() {
							override fun onFinish(data: Boolean?) {
								if (data != null && data)
									initStudentList()
							}

							override fun onError(e: Throwable) {
								Logs.wtf("onError: ", e)
								toastMessage(getString(R.string.error_delete_account, e.message), true)
							}
						})
					}
					.setNegativeButton(android.R.string.cancel, null)
					.show()
			true
		}
		setMainAccountPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
			val valueArray = Array(studentList.size) { i -> studentList[i].username }
			val oldMainIndex = studentList.indexOfFirst { s -> s.isMain }
			var newMainIndex = oldMainIndex
			AlertDialog.Builder(activity!!)
					.setTitle(R.string.title_set_main_account)
					.setSingleChoiceItems(valueArray, oldMainIndex) { _, which ->
						newMainIndex = which
					}
					.setPositiveButton(android.R.string.ok) { _, _ ->
						if (oldMainIndex == newMainIndex)
							return@setPositiveButton
						val oldMainStudent = studentList[oldMainIndex]
						val newMainStudent = studentList[newMainIndex]
						oldMainStudent.isMain = false
						newMainStudent.isMain = true
						StudentLocalDataSource.updateStudent(arrayListOf(oldMainStudent, newMainStudent), object : OnlyCompleteObserver<Boolean>() {
							override fun onFinish(data: Boolean?) {
								if (data != null && data)
									initStudentList()
							}

							override fun onError(e: Throwable) {
								Logs.wtf("onError: ", e)
								toastMessage(getString(R.string.error_set_main_account, e.message), true)
							}
						})
					}
					.show()
			true
		}
		enableMultiUserModePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
			val isEnableMultiUserMode = !enableMultiUserModePreference.isChecked
			if (isEnableMultiUserMode)
				AlertDialog.Builder(activity!!)
						.setTitle(" ")
						.setMessage(R.string.warning_enable_multi_user_mode)
						.setPositiveButton(R.string.action_open) { _, _ ->
							ConfigurationUtil.isEnableMultiUserMode = true
							setMainAccountPreference.isEnabled = false
						}
						.setNegativeButton(android.R.string.cancel) { _, _ ->
							enableMultiUserModePreference.isChecked = false
							ConfigurationUtil.isEnableMultiUserMode = false
							setMainAccountPreference.isEnabled = true
						}
						.setOnDismissListener {
							enableMultiUserModePreference.isChecked = ConfigurationUtil.isEnableMultiUserMode
							setMainAccountPreference.isEnabled = !ConfigurationUtil.isEnableMultiUserMode
						}
						.show()
			else {
				ConfigurationUtil.isEnableMultiUserMode = false
				setMainAccountPreference.isEnabled = true
			}
			true
		}
	}

	private fun initStudentList() {
		StudentLocalDataSource.queryAllStudentList {
			when (it.status) {
				Content -> {
					studentList.clear()
					studentList.addAll(it.data!!)
					initStudentCategory()
				}
				Error -> Logs.wtf("initStudentList: ", it.error)
				else -> {
				}
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK) {
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
			initStudentList()
		}
	}

	private fun initStudentCategory() {
		loggedStudentCategory.removeAll()
		studentList.forEach {
			val preference = Preference(activity)
			preference.isIconSpaceReserved = true
			preference.title = "${it.username}(${it.studentName})"
			if (it.isMain)
				preference.setIcon(R.drawable.ic_main_student)
			loggedStudentCategory.addPreference(preference)
		}
	}
}