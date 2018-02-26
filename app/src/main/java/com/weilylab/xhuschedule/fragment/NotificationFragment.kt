/*
 * Created by Mystery0 on 18-2-21 下午10:40.
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
 * Last modified 18-2-21 下午10:08
 */

package com.weilylab.xhuschedule.fragment

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.RingtonePreference
import android.preference.SwitchPreference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.NotificationService
import com.weilylab.xhuschedule.util.Settings
import java.util.*

/**
 * Created by myste.
 */
class NotificationFragment : BasePreferenceFragment() {
    private val TAG = "NotificationFragment"
    private lateinit var notificationSoundPreference: RingtonePreference
    private lateinit var notificationVibratePreference: SwitchPreference
    private lateinit var notificationTimePreference: Preference
    private lateinit var notificationExactTimePreference: SwitchPreference
    private lateinit var notificationTomorrowEnablePreference: SwitchPreference
    private lateinit var notificationExamEnablePreference: SwitchPreference
    private lateinit var notificationTomorrowTypePreference: ListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_notification)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        notificationSoundPreference = findPreferenceById(R.string.key_notification_sound) as RingtonePreference
        notificationVibratePreference = findPreferenceById(R.string.key_notification_vibrate) as SwitchPreference
        notificationTimePreference = findPreferenceById(R.string.key_notification_time)
        notificationExactTimePreference = findPreferenceById(R.string.key_notification_exact_time) as SwitchPreference
        notificationTomorrowEnablePreference = findPreferenceById(R.string.key_notification_for_tomorrow_enable) as SwitchPreference
        notificationExamEnablePreference = findPreferenceById(R.string.key_notification_for_exam_enable) as SwitchPreference
        notificationTomorrowTypePreference = findPreferenceById(R.string.key_notification_for_tomorrow_type) as ListPreference

        notificationSoundPreference.setDefaultValue(Settings.notificationSound)
        notificationSoundPreference.summary = getString(R.string.summary_notification_sound, getRingtoneName(Settings.notificationSound))
        notificationVibratePreference.isChecked = Settings.notificationVibrate
        notificationTimePreference.summary = getString(R.string.summary_notification_time, Settings.notificationTime)
        notificationExactTimePreference.isChecked = Settings.notificationExactTime
        notificationTomorrowEnablePreference.isChecked = Settings.isNotificationTomorrowEnable
        notificationTomorrowTypePreference.isEnabled = Settings.isNotificationTomorrowEnable
        notificationTomorrowTypePreference.setDefaultValue(resources.getStringArray(R.array.notification_tomorrow_type)[Settings.notificationTomorrowType])
        notificationTomorrowTypePreference.summary = getString(R.string.summary_notification_for_tomorrow_type, when (Settings.notificationTomorrowType) {
            0 -> "今日"
            1 -> "明日"
            else -> throw NullPointerException("Type error!")
        })

        notificationSoundPreference.setOnPreferenceChangeListener { _, newValue ->
            notificationSoundPreference.summary = getString(R.string.summary_notification_sound, getRingtoneName(newValue.toString()))
            Settings.notificationSound = newValue.toString()
            true
        }
        notificationVibratePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.notificationVibrate = !notificationVibratePreference.isChecked
            true
        }
        notificationTimePreference.setOnPreferenceClickListener {
            val hour: Int
            val minute: Int
            if (Settings.notificationTime != "") {
                val time = Settings.notificationTime.split(':')
                hour = time[0].toInt()
                minute = time[1].toInt()
            } else {
                val calendar = Calendar.getInstance(Locale.CHINA)
                hour = calendar.get(Calendar.HOUR_OF_DAY)
                minute = calendar.get(Calendar.MINUTE)
            }
            TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, newHour, newMinute ->
                val newHourString = if (newHour < 10) "0$newHour" else newHour.toString()
                val newMinuteString = if (newMinute < 10) "0$newMinute" else newMinute.toString()
                val newTime = "$newHourString:$newMinuteString"
                Settings.notificationTime = newTime
                notificationTimePreference.summary = getString(R.string.summary_notification_time, Settings.notificationTime)
            }, hour, minute, true).show()
            true
        }
        notificationExactTimePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.notificationExactTime = !notificationExactTimePreference.isChecked
            true
        }
        notificationTomorrowEnablePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.isNotificationTomorrowEnable = !notificationTomorrowEnablePreference.isChecked
            notificationTomorrowTypePreference.isEnabled = Settings.isNotificationTomorrowEnable
            if (Settings.isNotificationTomorrowEnable)
                activity.startService(Intent(activity, NotificationService::class.java))
            true
        }
        notificationExamEnablePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.isNotificationExamEnable = !notificationExamEnablePreference.isChecked
            if (Settings.isNotificationExamEnable)
                activity.startService(Intent(activity, NotificationService::class.java))
            true
        }
        notificationTomorrowTypePreference.setOnPreferenceChangeListener { _, newValue ->
            val index = resources.getStringArray(R.array.notification_tomorrow_type).indexOfFirst { it == newValue }
            Settings.notificationTomorrowType = index
            notificationTomorrowTypePreference.summary = getString(R.string.summary_notification_for_tomorrow_type, when (Settings.notificationTomorrowType) {
                0 -> "今日"
                1 -> "明日"
                else -> throw NullPointerException("Type error!")
            })
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}