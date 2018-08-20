package com.weilylab.xhuschedule.ui.fragment.settings

import android.app.Activity
import android.content.Intent
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.Status.*
import com.weilylab.xhuschedule.model.Student
import com.weilylab.xhuschedule.repository.local.StudentLocalDataSource
import com.weilylab.xhuschedule.ui.activity.LoginActivity
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.utils.LayoutRefreshConfigUtil
import com.weilylab.xhuschedule.utils.rxAndroid.RxObserver
import vip.mystery0.logs.Logs

class AccountSettingsFragment : BasePreferenceFragment(R.xml.preference_account) {
	companion object {
		const val ADD_ACCOUNT_CODE = 233
	}

	private lateinit var loggedStudentCategory: PreferenceCategory
	private lateinit var addAccountPreference: Preference
	private lateinit var delAccountPreference: Preference
	private lateinit var setMainAccountPreference: Preference
	private lateinit var enableMultiUserModePreference: CheckBoxPreference
	private val studentList = ArrayList<Student>()

	override fun initPreference() {
		super.initPreference()
		loggedStudentCategory = findPreferenceById(R.string.key_logged_account) as PreferenceCategory
		addAccountPreference = findPreferenceById(R.string.key_add_account)
		delAccountPreference = findPreferenceById(R.string.key_del_account)
		setMainAccountPreference = findPreferenceById(R.string.key_set_main_account)
		enableMultiUserModePreference = findPreferenceById(R.string.key_enable_multi_user_mode) as CheckBoxPreference

		initStudentList()
		setMainAccountPreference.isEnabled = !ConfigurationUtil.isEnableMultiUserMode
		enableMultiUserModePreference.isChecked = ConfigurationUtil.isEnableMultiUserMode
	}

	override fun monitor() {
		super.monitor()
		addAccountPreference.setOnPreferenceClickListener {
			startActivityForResult(Intent(activity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
			true
		}
		delAccountPreference.setOnPreferenceClickListener { _ ->
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
			val valueArray = Array(studentList.size) { i -> "${studentList[i].username}(${studentList[i].studentName})" }
			val checkedArray = BooleanArray(studentList.size) { false }
			AlertDialog.Builder(activity)
					.setTitle(R.string.title_del_account)
					.setMultiChoiceItems(valueArray, checkedArray) { _, which, isChecked ->
						checkedArray[which] = isChecked
					}
					.setPositiveButton(android.R.string.ok) { _, _ ->
						val needDeleteStudentList = ArrayList<Student>()
						checkedArray.forEachIndexed { index, bool ->
							if (bool) needDeleteStudentList.add(studentList[index])
						}
						StudentLocalDataSource.deleteStudent(needDeleteStudentList, object : RxObserver<Boolean>() {
							override fun onFinish(data: Boolean?) {
								if (data != null && data)
									initStudentList()
							}

							override fun onError(e: Throwable) {
								Logs.wtf("onError: ", e)
								Toast.makeText(activity, "删除失败，错误详情：${e.message}", Toast.LENGTH_LONG)
										.show()
							}
						})
					}
					.setNegativeButton(android.R.string.cancel, null)
					.show()
			true
		}
		setMainAccountPreference.setOnPreferenceClickListener { _ ->
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
			val valueArray = Array(studentList.size) { i -> studentList[i].username }
			val oldMainIndex = studentList.indexOfFirst { it.isMain }
			var newMainIndex = oldMainIndex
			AlertDialog.Builder(activity)
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
						StudentLocalDataSource.updateStudent(arrayListOf(oldMainStudent, newMainStudent), object : RxObserver<Boolean>() {
							override fun onFinish(data: Boolean?) {
								if (data != null && data)
									initStudentList()
							}

							override fun onError(e: Throwable) {
								Logs.wtf("onError: ", e)
								Toast.makeText(activity, "设置主用户失败，错误详情：${e.message}", Toast.LENGTH_LONG)
										.show()
							}
						})
					}
					.show()
			true
		}
		enableMultiUserModePreference.setOnPreferenceChangeListener { _, _ ->
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
			}
		}
	}

	private fun initStudentCategory() {
		loggedStudentCategory.removeAll()
		studentList.forEach {
			val preference = Preference(activity)
			if (it.isMain)
				preference.title = "${it.username}(${it.studentName})[主]"
			else
				preference.title = "${it.username}(${it.studentName})"
			loggedStudentCategory.addPreference(preference)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK) {
			LayoutRefreshConfigUtil.isRefreshBottomNavigationActivity = true
			initStudentList()
		}
	}
}