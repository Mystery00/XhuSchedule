/*
 * Created by Mystery0 on 4/6/18 12:10 AM.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 3/25/18 2:37 PM
 */

package com.weilylab.xhuschedule.fragment.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.SwitchPreference
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.LoginActivity
import com.weilylab.xhuschedule.classes.baseClass.Student
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.util.XhuFileUtil

/**
 * Created by myste.
 */
class AccountSettingsFragment : BasePreferenceFragment() {
    companion object {
        private const val ADD_ACCOUNT_CODE = 1
    }

    private lateinit var currentAccountCategory: PreferenceCategory
    private lateinit var addAccountPreference: Preference
    private lateinit var delAccountPreference: Preference
    private lateinit var setMainAccountPreference: Preference
    private lateinit var multiUserModePreference: SwitchPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_account)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentAccountCategory = findPreferenceById(R.string.key_current_account) as PreferenceCategory
        addAccountPreference = findPreferenceById(R.string.key_add_account)
        delAccountPreference = findPreferenceById(R.string.key_del_account)
        setMainAccountPreference = findPreferenceById(R.string.key_set_main_account)
        multiUserModePreference = findPreferenceById(R.string.key_enable_multi_user_mode) as SwitchPreference
        return super.onCreateView(inflater, container, savedInstanceState)
    }

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val userFile = XhuFileUtil.getStudentListFile(activity)
		val studentList = XhuFileUtil.getArrayListFromFile(userFile, Student::class.java)
		updateCategory(studentList)
		multiUserModePreference.isChecked = Settings.isEnableMultiUserMode

		addAccountPreference.setOnPreferenceClickListener {
			startActivityForResult(Intent(activity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
			true
		}
		delAccountPreference.setOnPreferenceClickListener {
			studentList.clear()
			studentList.addAll(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
			val valueArray = Array(studentList.size, { i -> studentList[i].username })
			val checkedArray = BooleanArray(studentList.size, { false })
			AlertDialog.Builder(activity)
					.setTitle(R.string.title_del_account)
					.setMultiChoiceItems(valueArray, checkedArray, { _, which, isChecked ->
						checkedArray[which] = isChecked
					})
					.setPositiveButton(android.R.string.ok, { _, _ ->
						val temp = ArrayList<Student>()
						checkedArray.forEachIndexed { index, b ->
							if (b)
								temp.add(studentList[index])
						}
						val showFile = XhuFileUtil.getShowStudentListFile(activity)
						val showList = XhuFileUtil.getArrayListFromFile(showFile, Student::class.java)
						val studentIterator = studentList.iterator()
						while (studentIterator.hasNext()) {
							val t = studentIterator.next()
							if (temp.contains(t))
								studentIterator.remove()
						}
						val showIterator = showList.iterator()
						while (showIterator.hasNext()) {
							val t = showIterator.next()
							var result = false
							temp.forEach {
								result = result || it.username == t.username
							}
							if (result)
								showIterator.remove()
						}
						val isSetMain = studentList.size == 0 || (0 until studentList.size).any { studentList[it].isMain }
						if (!isSetMain)
							studentList[0].isMain = true
						XhuFileUtil.saveObjectToFile(studentList, userFile)
						XhuFileUtil.saveObjectToFile(showList, showFile)
						studentList.clear()
						studentList.addAll(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
						updateCategory(studentList)
						ScheduleHelper.isUIChange = true
					})
					.setNegativeButton(android.R.string.cancel, null)
					.show()
			true
		}
		setMainAccountPreference.setOnPreferenceClickListener {
			studentList.clear()
			studentList.addAll(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
			val valueArray = Array(studentList.size, { i -> studentList[i].username })
			var mainIndex = (0 until studentList.size).firstOrNull { studentList[it].isMain } ?: 0
			AlertDialog.Builder(activity)
					.setTitle(R.string.title_set_main_account)
					.setSingleChoiceItems(valueArray, mainIndex, { _, which ->
						mainIndex = which
					})
					.setPositiveButton(android.R.string.ok, { _, _ ->
						studentList.forEachIndexed { index, student ->
							student.isMain = index == mainIndex
						}
						XhuFileUtil.saveObjectToFile(studentList, userFile)
						studentList.clear()
						studentList.addAll(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
						updateCategory(studentList)
						ScheduleHelper.isUIChange = true
					})
					.setNegativeButton(android.R.string.cancel, null)
					.show()
			true
		}
		multiUserModePreference.setOnPreferenceChangeListener { _, _ ->
			val isEnableMultiUserMode = !multiUserModePreference.isChecked
			if (isEnableMultiUserMode) {
				AlertDialog.Builder(activity)
						.setTitle(" ")
						.setMessage(R.string.warning_enable_multi_user_mode)
						.setPositiveButton(R.string.action_open, { _, _ ->
							Settings.isEnableMultiUserMode = true
							ScheduleHelper.isUIChange = true
						})
						.setNegativeButton(android.R.string.cancel, { _, _ ->
							multiUserModePreference.isChecked = false
							Settings.isEnableMultiUserMode = false
						})
						.setOnDismissListener {
							multiUserModePreference.isChecked = Settings.isEnableMultiUserMode
						}
						.show()
			} else {
				Settings.isEnableMultiUserMode = false
				ScheduleHelper.isUIChange = true
			}
			true
		}
	}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK) {
            ScheduleHelper.isUIChange = true
            val userFile = XhuFileUtil.getStudentListFile(activity)
            updateCategory(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
        }
    }

    private fun updateCategory(studentList: ArrayList<Student>) {
        currentAccountCategory.removeAll()
        studentList.forEach {
            val preference = Preference(activity)
            if (it.isMain)
                preference.title = "${it.username}(主)"
            else
                preference.title = it.username
            currentAccountCategory.addPreference(preference)
        }
    }
}