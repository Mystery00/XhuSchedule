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

package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.APP

/**
 * Created by myste.
 */
object Settings {
    private val sharedPreference = APP.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

    var firstWeekOfTerm: String//开学时间
        set(value) = sharedPreference.edit().putString("firstWeekOfTerm", value).apply()
        get() = sharedPreference.getString("firstWeekOfTerm", "2017-8-4")
    var isShowNot: Boolean//是否显示非本周课程
        set(value) = sharedPreference.edit().putBoolean("isShowNot", value).apply()
        get() = sharedPreference.getBoolean("isShowNot", false)
    var isFirstRun: Boolean//是否是第一次运行
        set(value) = sharedPreference.edit().putBoolean("isFirstRun", value).apply()
        get() = sharedPreference.getBoolean("isFirstRun", true)
    var userImg: String//用户头像路径
        set(value) = sharedPreference.edit().putString("userImg", value).apply()
        get() = sharedPreference.getString("userImg", "")
    var customHeaderImg: String//头部图片路径
        set(value) = sharedPreference.edit().putString("customHeaderImg", value).apply()
        get() = sharedPreference.getString("customHeaderImg", "")
    var customBackgroundImg: String//背景图片路径
        set(value) = sharedPreference.edit().putString("customBackgroundImg", value).apply()
        get() = sharedPreference.getString("customBackgroundImg", "")
    var customTableOpacity: Int//表格不透明度
        set(value) = sharedPreference.edit().putInt("customTableOpacity", value).apply()
        get() = sharedPreference.getInt("customTableOpacity", 154)
    var customTodayOpacity: Int//当天课程不透明度
        set(value) = sharedPreference.edit().putInt("customTodayOpacity", value).apply()
        get() = sharedPreference.getInt("customTodayOpacity", 154)
    var customTableTextColor: Int//表头文字颜色
        set(value) = sharedPreference.edit().putInt("customTableTextColor", value).apply()
        get() = sharedPreference.getInt("customTableTextColor", -1)
    var customTodayTextColor: Int//今日课程文字颜色
        set(value) = sharedPreference.edit().putInt("customTodayTextColor", value).apply()
        get() = sharedPreference.getInt("customTodayTextColor", -11184811)
    var customTextSize: Int//文字大小
        set(value) = sharedPreference.edit().putInt("customTextSize", value).apply()
        get() = sharedPreference.getInt("customTextSize", 12)
    var customTextHeight: Int//课程格子高度
        set(value) = sharedPreference.edit().putInt("customHeightSize", value).apply()
        get() = sharedPreference.getInt("customHeightSize", 72)
    var autoCheckUpdate: Boolean//自动检查更新
        set(value) = sharedPreference.edit().putBoolean("autoCheckUpdate", value).apply()
        get() = sharedPreference.getBoolean("autoCheckUpdate", true)
    var autoCheckLog: Boolean//自动检查崩溃日志
        set(value) = sharedPreference.edit().putBoolean("autoCheckLog", value).apply()
        get() = sharedPreference.getBoolean("autoCheckLog", true)
    var isEnableMultiUserMode: Boolean//是否启用多用户模式
        set(value) = sharedPreference.edit().putBoolean("isEnableMultiUserMode", value).apply()
        get() = sharedPreference.getBoolean("isEnableMultiUserMode", false)
    var isShowFailed: Boolean//是否显示未通过成绩
        set(value) = sharedPreference.edit().putBoolean("isShowFailed", value).apply()
        get() = sharedPreference.getBoolean("isShowFailed", true)
    var ignoreUpdate: Int//忽略更新的版本号
        set(value) = sharedPreference.edit().putInt("ignoreUpdate", value).apply()
        get() = sharedPreference.getInt("ignoreUpdate", 0)
    var shownNoticeID: String//查看过的公告id
        set(value) = sharedPreference.edit().putString("shownNoticeID", value).apply()
        get() = sharedPreference.getString("shownNoticeID", "")
}