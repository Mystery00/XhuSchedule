/*
 * Created by Mystery0 on 18-2-21 下午9:12.
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
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.APP

/**
 * Created by myste.
 */
object Settings {
    private val sharedPreference = APP.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

    var firstWeekOfTerm: String//开学时间
        set(value) = sharedPreference.edit().putString(Constants.FIRST_WEEK_OF_TERM, value).apply()
        get() = sharedPreference.getString(Constants.FIRST_WEEK_OF_TERM, "2017-8-4")
    var isShowNot: Boolean//是否显示非本周课程
        set(value) = sharedPreference.edit().putBoolean(Constants.IS_SHOW_NOT, value).apply()
        get() = sharedPreference.getBoolean(Constants.IS_SHOW_NOT, false)
    var isFirstRun: Boolean//是否是第一次运行
        set(value) = sharedPreference.edit().putBoolean(Constants.IS_FIRST_RUN, value).apply()
        get() = sharedPreference.getBoolean(Constants.IS_FIRST_RUN, true)
    var userImg: String//用户头像路径
        set(value) = sharedPreference.edit().putString(Constants.USER_IMG, value).apply()
        get() = sharedPreference.getString(Constants.USER_IMG, "")
    var customBackgroundImg: String//背景图片路径
        set(value) = sharedPreference.edit().putString(Constants.CUSTOM_BACKGROUND_IMG, value).apply()
        get() = sharedPreference.getString(Constants.CUSTOM_BACKGROUND_IMG, "")
    var customTableOpacity: Int//表格不透明度
        set(value) = sharedPreference.edit().putInt(Constants.CUSTOM_TABLE_OPACITY, value).apply()
        get() = sharedPreference.getInt(Constants.CUSTOM_TABLE_OPACITY, 154)
    var customTodayOpacity: Int//当天课程不透明度
        set(value) = sharedPreference.edit().putInt(Constants.CUSTOM_TODAY_OPACITY, value).apply()
        get() = sharedPreference.getInt(Constants.CUSTOM_TODAY_OPACITY, 154)
    var customTableTextColor: Int//表头文字颜色
        set(value) = sharedPreference.edit().putInt(Constants.CUSTOM_TABLE_TEXT_COLOR, value).apply()
        get() = sharedPreference.getInt(Constants.CUSTOM_TABLE_TEXT_COLOR, -1)
    var customTodayTextColor: Int//今日课程文字颜色
        set(value) = sharedPreference.edit().putInt(Constants.CUSTOM_TODAY_TEXT_COLOR, value).apply()
        get() = sharedPreference.getInt(Constants.CUSTOM_TODAY_TEXT_COLOR, -11184811)
    var customTextSize: Int//文字大小
        set(value) = sharedPreference.edit().putInt(Constants.CUSTOM_TEXT_SIZE, value).apply()
        get() = sharedPreference.getInt(Constants.CUSTOM_TEXT_SIZE, 12)
    var customTextHeight: Int//课程格子高度
        set(value) = sharedPreference.edit().putInt(Constants.CUSTOM_HEIGHT_SIZE, value).apply()
        get() = sharedPreference.getInt(Constants.CUSTOM_HEIGHT_SIZE, 72)
    var autoCheckUpdate: Boolean//自动检查更新
        set(value) = sharedPreference.edit().putBoolean(Constants.AUTO_CHECK_UPDATE, value).apply()
        get() = sharedPreference.getBoolean(Constants.AUTO_CHECK_UPDATE, true)
    var autoCheckLog: Boolean//自动检查崩溃日志
        set(value) = sharedPreference.edit().putBoolean(Constants.AUTO_CHECK_LOG, value).apply()
        get() = sharedPreference.getBoolean(Constants.AUTO_CHECK_LOG, true)
    var isEnableMultiUserMode: Boolean//是否启用多用户模式
        set(value) = sharedPreference.edit().putBoolean(Constants.IS_ENABLE_MULTI_USER_MODE, value).apply()
        get() = sharedPreference.getBoolean(Constants.IS_ENABLE_MULTI_USER_MODE, false)
    var isShowFailed: Boolean//是否显示未通过成绩
        set(value) = sharedPreference.edit().putBoolean(Constants.IS_SHOW_FAILED, value).apply()
        get() = sharedPreference.getBoolean(Constants.IS_SHOW_FAILED, true)
    var isAutoSelect: Boolean//是否自动查询数据
        set(value) = sharedPreference.edit().putBoolean(Constants.IS_AUTO_SELECT, value).apply()
        get() = sharedPreference.getBoolean(Constants.IS_AUTO_SELECT, true)
    var ignoreUpdate: Int//忽略更新的版本号
        set(value) = sharedPreference.edit().putInt(Constants.IGNORE_UPDATE, value).apply()
        get() = sharedPreference.getInt(Constants.IGNORE_UPDATE, 0)
    var shownNoticeID: String//查看过的公告id
        set(value) = sharedPreference.edit().putString(Constants.SHOW_NOTICE_ID, value).apply()
        get() = sharedPreference.getString(Constants.SHOW_NOTICE_ID, "")
    var notificationSound: String//通知铃声
        set(value) = sharedPreference.edit().putString(Constants.NOTIFICATION_SOUND, value).apply()
        get() = sharedPreference.getString(Constants.NOTIFICATION_SOUND, "content://settings/system/notification_sound")
    var notificationVibrate: Boolean//是否震动
        set(value) = sharedPreference.edit().putBoolean(Constants.NOTIFICATION_VIBRATE, value).apply()
        get() = sharedPreference.getBoolean(Constants.NOTIFICATION_VIBRATE, true)
    var notificationTime: String//提醒明天课程时间
        set(value) = sharedPreference.edit().putString(Constants.NOTIFICATION_TIME, value).apply()
        get() = sharedPreference.getString(Constants.NOTIFICATION_TIME, "06:00")
    var notificationExactTime: Boolean//准时提醒
        set(value) = sharedPreference.edit().putBoolean(Constants.NOTIFICATION_EXACT_TIME, value).apply()
        get() = sharedPreference.getBoolean(Constants.NOTIFICATION_EXACT_TIME, false)
    var isNotificationTomorrowEnable: Boolean//是否提醒明天课程
        set(value) = sharedPreference.edit().putBoolean(Constants.NOTIFICATION_TOMORROW_ENABLE, value).apply()
        get() = sharedPreference.getBoolean(Constants.NOTIFICATION_TOMORROW_ENABLE, true)
    var isNotificationExamEnable: Boolean//是否提醒考试
        set(value) = sharedPreference.edit().putBoolean(Constants.NOTIFICATION_EXAM_ENABLE, value).apply()
        get() = sharedPreference.getBoolean(Constants.NOTIFICATION_EXAM_ENABLE, true)
    var notificationTomorrowType: Int//提醒消息类型
        set(value) = sharedPreference.edit().putInt(Constants.NOTIFICATION_TOMORROW_TYPE, value).apply()
        get() = sharedPreference.getInt(Constants.NOTIFICATION_TOMORROW_TYPE, 0)
}