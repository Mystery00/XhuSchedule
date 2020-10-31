/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

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
import com.weilylab.xhuschedule.model.event.UI
import com.weilylab.xhuschedule.model.event.UIConfigEvent
import com.weilylab.xhuschedule.ui.activity.LoginActivity
import com.weilylab.xhuschedule.utils.ConfigurationUtil
import com.weilylab.xhuschedule.viewmodel.SettingsViewModel
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vip.mystery0.logs.Logs
import vip.mystery0.rx.DataObserver

class AccountSettingsFragment : XhuBasePreferenceFragment(R.xml.preference_account) {
    companion object {
        const val ADD_ACCOUNT_CODE = 233
    }

    private val eventBus: EventBus by inject()

    private val settingsViewModel: SettingsViewModel by viewModel()

    private val loggedStudentCategory by lazy { findPreferenceById<PreferenceCategory>(R.string.key_logged_account) }
    private val addAccountPreference by lazy { findPreferenceById<Preference>(R.string.key_add_account) }
    private val delAccountPreference by lazy { findPreferenceById<Preference>(R.string.key_del_account) }
    private val setMainAccountPreference by lazy { findPreferenceById<Preference>(R.string.key_set_main_account) }
    private val enableMultiUserModePreference by lazy { findPreferenceById<CheckBoxPreference>(R.string.key_enable_multi_user_mode) }

    override fun initPreference() {
        super.initPreference()
        initViewModel()
        settingsViewModel.initStudentList()
        setMainAccountPreference.isEnabled = !ConfigurationUtil.isEnableMultiUserMode
        enableMultiUserModePreference.isChecked = ConfigurationUtil.isEnableMultiUserMode
    }

    private fun initViewModel() {
        settingsViewModel.studentList.observe(requireActivity(), object : DataObserver<List<Student>> {
            override fun contentNoEmpty(data: List<Student>) {
                super.contentNoEmpty(data)
                initStudentCategory(data)
            }

            override fun empty() {
                super.empty()
                initStudentCategory(emptyList())
            }

            override fun error(e: Throwable?) {
                super.error(e)
                Logs.w(e)
            }
        })
    }

    override fun monitor() {
        super.monitor()
        addAccountPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivityForResult(Intent(activity, LoginActivity::class.java), ADD_ACCOUNT_CODE)
            true
        }
        delAccountPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            settingsViewModel.studentList.value?.data?.let {
                val valueArray = Array(it.size) { i -> "${it[i].username}(${it[i].studentName})" }
                val checkedArray = BooleanArray(it.size) { false }
                AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.title_del_account)
                        .setMultiChoiceItems(valueArray, checkedArray) { _, which, isChecked ->
                            checkedArray[which] = isChecked
                        }
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            val needDeleteStudentList = ArrayList<Student>()
                            checkedArray.forEachIndexed { index, bool ->
                                if (bool) needDeleteStudentList.add(it[index])
                            }
                            settingsViewModel.deleteStudentList(needDeleteStudentList)
                            eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
            }
            true
        }
        setMainAccountPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            settingsViewModel.studentList.value?.data?.let {
                val valueArray = Array(it.size) { i -> it[i].username }
                val oldMainIndex = it.indexOfFirst { s -> s.isMain }
                var newMainIndex = oldMainIndex
                AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.title_set_main_account)
                        .setSingleChoiceItems(valueArray, oldMainIndex) { _, which ->
                            newMainIndex = which
                        }
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            if (oldMainIndex == newMainIndex)
                                return@setPositiveButton
                            val oldMainStudent = it[oldMainIndex]
                            val newMainStudent = it[newMainIndex]
                            oldMainStudent.isMain = false
                            newMainStudent.isMain = true
                            settingsViewModel.updateStudentList(arrayListOf(oldMainStudent, newMainStudent))
                            eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
                        }
                        .show()
            }
            true
        }
        enableMultiUserModePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            val isEnableMultiUserMode = !enableMultiUserModePreference.isChecked
            if (isEnableMultiUserMode)
                AlertDialog.Builder(requireActivity())
                        .setTitle(" ")
                        .setMessage(R.string.warning_enable_multi_user_mode)
                        .setPositiveButton(R.string.action_open) { _, _ ->
                            ConfigurationUtil.isEnableMultiUserMode = true
                            setMainAccountPreference.isEnabled = false
                            eventBus.post(UIConfigEvent(arrayListOf(UI.MAIN_INIT)))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK) {
            settingsViewModel.initStudentList()
        }
    }

    private fun initStudentCategory(studentList: List<Student>) {
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