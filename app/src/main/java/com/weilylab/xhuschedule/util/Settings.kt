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
import androidx.core.content.edit
import com.weilylab.xhuschedule.newPackage.config.APP

/**
 * Created by myste.
 */
object Settings {
	private val sharedPreference = APP.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_SETTINGS, Context.MODE_PRIVATE)

	var firstWeekOfTerm: String//开学时间
		set(value) {
			sharedPreference.edit { putString(Constants.FIRST_WEEK_OF_TERM, value) }
		}
		get() = sharedPreference.getString(Constants.FIRST_WEEK_OF_TERM, Constants.DEFAULT_TERM_START_DATE)
	var isShowNot: Boolean//是否显示非本周课程
		set(value) {
			sharedPreference.edit { putBoolean(Constants.IS_SHOW_NOT, value) }
		}
		get() = sharedPreference.getBoolean(Constants.IS_SHOW_NOT, false)
	var isFirstRun: Boolean//是否是第一次运行
		set(value) {
			sharedPreference.edit { putBoolean(Constants.IS_FIRST_RUN, value) }
		}
		get() = sharedPreference.getBoolean(Constants.IS_FIRST_RUN, true)
	var isFirstEnter: Boolean//是否是第一次运行2.0
		set(value) {
			sharedPreference.edit { putBoolean(Constants.IS_FIRST_ENTER, value) }
		}
		get() = sharedPreference.getBoolean(Constants.IS_FIRST_ENTER, true)
	var isFirstRun210: Boolean
		set(value) {
			sharedPreference.edit { putBoolean(Constants.IS_FIRST_RUN_210, value) }
		}
		get() = sharedPreference.getBoolean(Constants.IS_FIRST_RUN_210, true)
	var isFirstEnterToday: String//是否是今天第一次运行
		set(value) {
			sharedPreference.edit { putString(Constants.IS_FIRST_ENTER_TODAY, value) }
		}
		get() = sharedPreference.getString(Constants.IS_FIRST_ENTER_TODAY, "")
	var userImg: String//用户头像路径
		set(value) {
			sharedPreference.edit { putString(Constants.USER_IMG, value) }
		}
		get() = sharedPreference.getString(Constants.USER_IMG, "")
	var customBackgroundImg: String//背景图片路径
		set(value) {
			sharedPreference.edit { putString(Constants.CUSTOM_BACKGROUND_IMG, value) }
		}
		get() = sharedPreference.getString(Constants.CUSTOM_BACKGROUND_IMG, "")
	var customTableOpacity: Int//表格不透明度
		set(value) {
			sharedPreference.edit { putInt(Constants.CUSTOM_TABLE_OPACITY, value) }
		}
		get() = sharedPreference.getInt(Constants.CUSTOM_TABLE_OPACITY, Constants.DEFAULT_OPACITY)
	var customTodayOpacity: Int//当天课程不透明度
		set(value) {
			sharedPreference.edit { putInt(Constants.CUSTOM_TODAY_OPACITY, value) }
		}
		get() = sharedPreference.getInt(Constants.CUSTOM_TODAY_OPACITY, Constants.DEFAULT_OPACITY)
	var customTableTextColor: Int//表头文字颜色
		set(value) {
			sharedPreference.edit { putInt(Constants.CUSTOM_TABLE_TEXT_COLOR, value) }
		}
		get() = sharedPreference.getInt(Constants.CUSTOM_TABLE_TEXT_COLOR, Constants.DEFAULT_COLOR_TEXT_TABLE)
	var customTodayTextColor: Int//今日课程文字颜色
		set(value) {
			sharedPreference.edit { putInt(Constants.CUSTOM_TODAY_TEXT_COLOR, value) }
		}
		get() = sharedPreference.getInt(Constants.CUSTOM_TODAY_TEXT_COLOR, Constants.DEFAULT_COLOR_TEXT_TODAY)
	var customTextSize: Int//文字大小
		set(value) {
			sharedPreference.edit { putInt(Constants.CUSTOM_TEXT_SIZE, value) }
		}
		get() = sharedPreference.getInt(Constants.CUSTOM_TEXT_SIZE, Constants.DEFAULT_SIZE_TEXT)
	var customTableItemHeight: Int//课程格子高度
		set(value) {
			sharedPreference.edit { putInt(Constants.CUSTOM_HEIGHT_SIZE, value) }
		}
		get() = sharedPreference.getInt(Constants.CUSTOM_HEIGHT_SIZE, Constants.DEFAULT_SIZE_HEIGHT)
	var customTableItemWidth: Int//格子宽度，-1表示自适应
		set(value) {
			sharedPreference.edit { putInt(Constants.CUSTOM_TABLE_ITEM_WIDTH, value) }
		}
		get() = sharedPreference.getInt(Constants.CUSTOM_TABLE_ITEM_WIDTH, -1)
	var autoCheckUpdate: Boolean//自动检查更新
		set(value) {
			sharedPreference.edit { putBoolean(Constants.AUTO_CHECK_UPDATE, value) }
		}
		get() = sharedPreference.getBoolean(Constants.AUTO_CHECK_UPDATE, true)
	var isEnableMultiUserMode: Boolean//是否启用多用户模式
		set(value) {
			sharedPreference.edit { putBoolean(Constants.IS_ENABLE_MULTI_USER_MODE, value) }
		}
		get() = sharedPreference.getBoolean(Constants.IS_ENABLE_MULTI_USER_MODE, false)
	var isShowFailed: Boolean//是否显示未通过成绩
		set(value) {
			sharedPreference.edit { putBoolean(Constants.IS_SHOW_FAILED, value) }
		}
		get() = sharedPreference.getBoolean(Constants.IS_SHOW_FAILED, true)
	var isAutoSelect: Boolean//是否自动查询数据
		set(value) {
			sharedPreference.edit { putBoolean(Constants.IS_AUTO_SELECT, value) }
		}
		get() = sharedPreference.getBoolean(Constants.IS_AUTO_SELECT, true)
	var ignoreUpdate: Int//忽略更新的版本号
		set(value) {
			sharedPreference.edit { putInt(Constants.IGNORE_UPDATE, value) }
		}
		get() = sharedPreference.getInt(Constants.IGNORE_UPDATE, 0)
	var shownNoticeID: String//查看过的公告id
		set(value) {
			sharedPreference.edit { putString(Constants.SHOW_NOTICE_ID, value) }
		}
		get() = sharedPreference.getString(Constants.SHOW_NOTICE_ID, "")
	var notificationSound: String//通知铃声
		set(value) {
			sharedPreference.edit { putString(Constants.NOTIFICATION_SOUND, value) }
		}
		get() = sharedPreference.getString(Constants.NOTIFICATION_SOUND, Constants.DEFAULT_NOTIFICATION_SOUND)
	var notificationVibrate: Boolean//是否震动
		set(value) {
			sharedPreference.edit { putBoolean(Constants.NOTIFICATION_VIBRATE, value) }
		}
		get() = sharedPreference.getBoolean(Constants.NOTIFICATION_VIBRATE, true)
	var notificationTime: String//提醒明天课程时间
		set(value) {
			sharedPreference.edit { putString(Constants.NOTIFICATION_TIME, value) }
		}
		get() = sharedPreference.getString(Constants.NOTIFICATION_TIME, Constants.DEFAULT_NOTIFICATION_TIME)
	var notificationExactTime: Boolean//准时提醒
		set(value) {
			sharedPreference.edit { putBoolean(Constants.NOTIFICATION_EXACT_TIME, value) }
		}
		get() = sharedPreference.getBoolean(Constants.NOTIFICATION_EXACT_TIME, false)
	var isNotificationTomorrowEnable: Boolean//是否提醒明天课程
		set(value) {
			sharedPreference.edit { putBoolean(Constants.NOTIFICATION_TOMORROW_ENABLE, value) }
		}
		get() = sharedPreference.getBoolean(Constants.NOTIFICATION_TOMORROW_ENABLE, true)
	var isNotificationExamEnable: Boolean//是否提醒考试
		set(value) {
			sharedPreference.edit { putBoolean(Constants.NOTIFICATION_EXAM_ENABLE, value) }
		}
		get() = sharedPreference.getBoolean(Constants.NOTIFICATION_EXAM_ENABLE, true)
	var notificationTomorrowType: Int//提醒消息类型
		set(value) {
			sharedPreference.edit { putInt(Constants.NOTIFICATION_TOMORROW_TYPE, value) }
		}
		get() = sharedPreference.getInt(Constants.NOTIFICATION_TOMORROW_TYPE, 1)
	var splashImage: String//启动页图片
		set(value) {
			sharedPreference.edit { putString(Constants.SPLASH_IMAGE_FILE_NAME, value) }
		}
		get() = sharedPreference.getString(Constants.SPLASH_IMAGE_FILE_NAME, "")
	var splashTime: Long//启动页显示时间
		set(value) {
			sharedPreference.edit { putLong(Constants.SPLASH_TIME, value) }
		}
		get() = sharedPreference.getLong(Constants.SPLASH_TIME, 3000)
	var splashLocationUrl: String//启动页跳转的链接
		set(value) {
			sharedPreference.edit { putString(Constants.SPLASH_LOCATION_URL, value) }
		}
		get() = sharedPreference.getString(Constants.SPLASH_LOCATION_URL, "")
	var currentTheme: String//当前使用的主题
		set(value) {
			sharedPreference.edit { putString(Constants.APPLIED_THEME, value) }
		}
		get() = sharedPreference.getString(Constants.APPLIED_THEME, "null")
}