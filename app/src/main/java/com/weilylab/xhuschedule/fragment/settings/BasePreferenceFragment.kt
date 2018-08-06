/*
 * Created by Mystery0 on 4/6/18 12:16 AM.
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
 * Last modified 4/6/18 12:10 AM
 */

package com.weilylab.xhuschedule.fragment.settings

import androidx.annotation.StringRes
import android.media.RingtoneManager
import android.net.Uri
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.Constants

abstract class BasePreferenceFragment : PreferenceFragment() {
    fun findPreferenceById(@StringRes id: Int): Preference = findPreference(getString(id))

    // 获取提示音名称
    fun getRingtoneName(uriString: String): String {
        return when (uriString) {
            "" -> getString(R.string.hint_notification_none)
            Constants.NOTIFICATION_SYSTEM_SOUND -> getString(R.string.hint_notification_system)
            else -> RingtoneManager.getRingtone(activity, Uri.parse(uriString)).getTitle(activity)
        }
    }
}