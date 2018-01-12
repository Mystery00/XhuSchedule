/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings
import com.weilylab.xhuschedule.view.CustomDatePicker
import java.util.*

/**
 * Created by myste.
 */
class ClassSettingsFragment : PreferenceFragment() {
    private lateinit var firstDayPreference: Preference
    private lateinit var showNotPreference: SwitchPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_class)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        firstDayPreference = findPreference(getString(R.string.key_first_day))
        showNotPreference = findPreference(getString(R.string.key_show_not)) as SwitchPreference
        val date = Settings.firstWeekOfTerm.split('-')
        firstDayPreference.summary = date[0] + '-' + (date[1].toInt() + 1) + '-' + date[2]
        showNotPreference.isChecked = Settings.isShowNot
        firstDayPreference.setOnPreferenceClickListener {
            val calendar = Calendar.getInstance(Locale.CHINA)
            calendar.set(date[0].toInt(), date[1].toInt(), date[2].toInt(), 0, 0, 0)
            val view = View.inflate(activity, R.layout.dialog_date_picker, null)
            val datePicker: CustomDatePicker = view.findViewById(R.id.datePicker)
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)
            val dialog = AlertDialog.Builder(activity)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
            dialog.show()
            if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, 0, 0, 0)
                    when {
                        calendar.after(Calendar.getInstance()) -> Snackbar.make(datePicker, R.string.error_time_after, Snackbar.LENGTH_SHORT)
                                .show()
                        else -> {
                            val dayWeek = calendar.get(Calendar.DAY_OF_WEEK)
                            if (dayWeek == Calendar.SUNDAY)
                                calendar.add(Calendar.DAY_OF_MONTH, -1)
                            calendar.firstDayOfWeek = Calendar.MONDAY
                            val day = calendar.get(Calendar.DAY_OF_WEEK)
                            calendar.add(Calendar.DATE, calendar.firstDayOfWeek - day)
                            Settings.firstWeekOfTerm = calendar.get(Calendar.YEAR).toString() + '-' + calendar.get(Calendar.MONTH).toString() + '-' + calendar.get(Calendar.DAY_OF_MONTH).toString()
                            firstDayPreference.summary = calendar.get(Calendar.YEAR).toString() + '-' + (calendar.get(Calendar.MONTH) + 1).toString() + '-' + calendar.get(Calendar.DAY_OF_MONTH).toString()
                            //更新小部件
                            activity.sendBroadcast(Intent("android.appwidget.action.APPWIDGET_UPDATE"))
                            dialog.dismiss()
                        }
                    }
                }
            }
            true
        }
        showNotPreference.setOnPreferenceChangeListener { _, _ ->
            Settings.isShowNot = !Settings.isShowNot
            ScheduleHelper.isUIChange = true
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}