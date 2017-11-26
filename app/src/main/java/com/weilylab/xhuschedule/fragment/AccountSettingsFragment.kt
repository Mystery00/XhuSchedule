package com.weilylab.xhuschedule.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.MultiSelectListPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.activity.LoginActivity
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
    private lateinit var delAccountPreference: MultiSelectListPreference
    private lateinit var managerAccountPreference: MultiSelectListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_account)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentAccountCategory = findPreference(getString(R.string.key_current_account)) as PreferenceCategory
        addAccountPreference = findPreference(getString(R.string.key_add_account))
        delAccountPreference = findPreference(getString(R.string.key_del_account)) as MultiSelectListPreference
        managerAccountPreference = findPreference(getString(R.string.key_show_account_manager)) as MultiSelectListPreference
        val userFile = File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "user")
        XhuFileUtil.getStudentsFromFile(userFile)
                .forEach {
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
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ACCOUNT_CODE && resultCode == Activity.RESULT_OK) {
            val userFile = File(activity.filesDir.absolutePath + File.separator + "data" + File.separator + "user")
            currentAccountCategory.removeAll()
            XhuFileUtil.getStudentsFromFile(userFile)
                    .forEach {
                        val preference = Preference(activity)
                        preference.title = it.name
                        preference.summary = it.username
                        currentAccountCategory.addPreference(preference)
                    }
        }
    }
}