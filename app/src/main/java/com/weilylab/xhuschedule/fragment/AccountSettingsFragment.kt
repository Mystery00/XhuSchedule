/*
 * Created by Mystery0 on 17-11-27 上午3:50.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-11-27 上午1:24
 */

package com.weilylab.xhuschedule.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.LoginActivity
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.XhuFileUtil
import vip.mystery0.tools.logs.Logs
import java.io.File

/**
 * Created by myste.
 */
class AccountSettingsFragment : PreferenceFragment() {
    companion object {
        private val TAG = "AccountSettingsFragment"
        private val ADD_ACCOUNT_CODE = 1
    }

    private lateinit var currentAccountCategory: PreferenceCategory
    private lateinit var addAccountPreference: Preference
    private lateinit var delAccountPreference: Preference
    private lateinit var managerAccountPreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_account)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentAccountCategory = findPreference(getString(R.string.key_current_account)) as PreferenceCategory
        addAccountPreference = findPreference(getString(R.string.key_add_account))
        delAccountPreference = findPreference(getString(R.string.key_del_account))
        managerAccountPreference = findPreference(getString(R.string.key_show_account_manager))
        val userFile = File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "user")
        val studentList = XhuFileUtil.getArrayListFromFile(userFile, Student::class.java)
        studentList.forEach {
            val preference = Preference(activity)
            preference.title = it.name
            preference.summary = it.username
            currentAccountCategory.addPreference(preference)
        }
        addAccountPreference.setOnPreferenceClickListener {
            startActivityForResult(Intent(activity, LoginActivity::class.java)
                    .putExtra("isAddAccount", true), ADD_ACCOUNT_CODE)
            true
        }
        delAccountPreference.setOnPreferenceClickListener {
            studentList.clear()
            studentList.addAll(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
            val valueArray = Array(studentList.size, { i -> "${studentList[i].name}(${studentList[i].username})" })
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
                        val showFile = File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "show_user")
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
                        XhuFileUtil.saveObjectToFile(studentList, userFile)
                        XhuFileUtil.saveObjectToFile(showList, showFile)
                        studentList.clear()
                        studentList.addAll(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
                        currentAccountCategory.removeAll()
                        studentList.forEach {
                            val preference = Preference(activity)
                            preference.title = it.name
                            preference.summary = it.username
                            currentAccountCategory.addPreference(preference)
                        }
                        ScheduleHelper.isUIChange = true
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            true
        }
        managerAccountPreference.setOnPreferenceClickListener {
            studentList.clear()
            studentList.addAll(XhuFileUtil.getArrayListFromFile(userFile, Student::class.java))
            val showFile = File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "show_user")
            val showList = XhuFileUtil.getArrayListFromFile(showFile, Student::class.java)
            val valueArray = Array(studentList.size, { i -> "${studentList[i].name}(${studentList[i].username})" })
            val checkedArray = BooleanArray(studentList.size, { i ->
                var result = false
                showList.forEach {
                    result = result || it.username == studentList[i].username
                }
                result
            })
            AlertDialog.Builder(activity)
                    .setTitle(R.string.title_account_manager)
                    .setMultiChoiceItems(valueArray, checkedArray, { _, which, isChecked ->
                        Logs.i(TAG, "onCreateView: " + which)
                        Logs.i(TAG, "onCreateView: " + isChecked)
                        checkedArray[which] = isChecked
                    })
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        val temp = ArrayList<Student>()
                        checkedArray.forEachIndexed { index, b ->
                            if (b)
                                temp.add(studentList[index])
                        }
                        XhuFileUtil.saveObjectToFile(temp, showFile)
                        ScheduleHelper.isUIChange = true
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK) {
            ScheduleHelper.isUIChange = true
            val userFile = File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "user")
            currentAccountCategory.removeAll()
            XhuFileUtil.getArrayListFromFile(userFile, Student::class.java)
                    .forEach {
                        val preference = Preference(activity)
                        preference.title = it.name
                        preference.summary = it.username
                        currentAccountCategory.addPreference(preference)
                    }
        }
    }
}