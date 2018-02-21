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

import android.net.Uri
import android.os.Bundle
import android.preference.RingtonePreference
import android.preference.SwitchPreference
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.Settings
import vip.mystery0.tools.logs.Logs

/**
 * Created by myste.
 */
class NotificationFragment : BasePreferenceFragment() {
    private val TAG = "NotificationFragment"
    private lateinit var notificationSoundPreference: RingtonePreference
    private lateinit var notificationVibratePreference: SwitchPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_notification)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        notificationSoundPreference = findPreferenceById(R.string.key_notification_sound) as RingtonePreference
        notificationVibratePreference = findPreferenceById(R.string.key_notification_vibrate) as SwitchPreference

        notificationSoundPreference.setDefaultValue(Settings.notificationSound)
        notificationSoundPreference.summary = getString(R.string.summary_notification_sound, getRingtoneName(Uri.parse(Settings.notificationSound)))
        notificationVibratePreference.isChecked = Settings.notificationVibrate
        notificationVibratePreference.setOnPreferenceChangeListener { _, _ ->
            Settings.notificationVibrate = !notificationVibratePreference.isChecked
            true
        }

        notificationSoundPreference.setOnPreferenceChangeListener { _, newValue ->
            notificationSoundPreference.summary = getString(R.string.summary_notification_sound, getRingtoneName(Uri.parse(newValue.toString())))
            Settings.notificationSound = newValue.toString()
            Logs.i(TAG, "onCreateView: $newValue")
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}