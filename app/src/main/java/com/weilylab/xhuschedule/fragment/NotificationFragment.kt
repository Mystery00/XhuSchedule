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
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.RingtonePreference
import android.preference.SwitchPreference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.Settings
import vip.mystery0.tools.logs.Logs
import java.util.*

/**
 * Created by myste.
 */
class NotificationFragment : BasePreferenceFragment() {
    private val TAG = "NotificationFragment"
    private lateinit var notificationSoundPreference: RingtonePreference
    private lateinit var notificationVibratePreference: SwitchPreference
    private lateinit var notificationTomorrowEnablePreference: SwitchPreference
    private lateinit var notificationTomorrowTypePreference: ListPreference
    private lateinit var notificationTomorrowTimePreference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_notification)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        notificationSoundPreference = findPreferenceById(R.string.key_notification_sound) as RingtonePreference
        notificationVibratePreference = findPreferenceById(R.string.key_notification_vibrate) as SwitchPreference
        notificationTomorrowEnablePreference = findPreferenceById(R.string.key_notification_for_tomorrow_enable) as SwitchPreference
        notificationTomorrowTypePreference = findPreferenceById(R.string.key_notification_for_tomorrow_type) as ListPreference
        notificationTomorrowTimePreference = findPreferenceById(R.string.key_notification_for_tomorrow_time)

        notificationSoundPreference.setDefaultValue(Settings.notificationSound)
        notificationSoundPreference.summary = getString(R.string.summary_notification_sound, getRingtoneName(Settings.notificationSound))
        notificationVibratePreference.isChecked = Settings.notificationVibrate
        notificationVibratePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.notificationVibrate = !notificationVibratePreference.isChecked
            true
        }
        notificationTomorrowEnablePreference.isChecked = Settings.isNotificationTomorrowEnable
        notificationTomorrowTimePreference.isEnabled = Settings.isNotificationTomorrowEnable
        notificationTomorrowTypePreference.isEnabled = Settings.isNotificationTomorrowEnable
        notificationTomorrowTypePreference.setDefaultValue(resources.getStringArray(R.array.notification_tomorrow_type)[Settings.notificationTomorrowType])
        notificationTomorrowTypePreference.summary = getString(R.string.summary_notification_for_tomorrow_type, when (Settings.notificationTomorrowType) {
            0 -> "今日"
            1 -> "明日"
            else -> throw NullPointerException("Type error!")
        })
        notificationTomorrowTimePreference.summary = getString(R.string.summary_notification_for_tomorrow_time, Settings.notificationTomorrowTime)

        notificationSoundPreference.setOnPreferenceChangeListener { _, newValue ->
            notificationSoundPreference.summary = getString(R.string.summary_notification_sound, getRingtoneName(newValue.toString()))
            Settings.notificationSound = newValue.toString()
            true
        }
        notificationTomorrowEnablePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.isNotificationTomorrowEnable = !notificationTomorrowEnablePreference.isChecked
            notificationTomorrowTimePreference.isEnabled = Settings.isNotificationTomorrowEnable
            notificationTomorrowTypePreference.isEnabled = Settings.isNotificationTomorrowEnable
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
        notificationTomorrowTimePreference.setOnPreferenceClickListener {
            val hour: Int
            val minute: Int
            if (Settings.notificationTomorrowTime != "") {
                val time = Settings.notificationTomorrowTime.split(':')
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
                Settings.notificationTomorrowTime = newTime
                notificationTomorrowTimePreference.summary = getString(R.string.summary_notification_for_tomorrow_time, Settings.notificationTomorrowTime)
            }, hour, minute, true).show()
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}