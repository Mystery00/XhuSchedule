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

object Constants {
    const val QQ_API_KEY = "1106663023"
    const val WEIBO_API_KEY = "2170085314"
    const val WEIXIN_API_KEY = ""

    const val NOTIFICATION_SYSTEM_SOUND = "content://settings/system/notification_sound"

    const val ACTION_WIDGET_UPDATE_BROADCAST = "android.appwidget.action.APPWIDGET_UPDATE"
    const val ACTION_BOOT_COMPLETED="android.intent.action.BOOT_COMPLETED"

    const val NOTIFICATION_CHANNEL_ID_DEFAULT = "XhuSchedule-Default"
    const val NOTIFICATION_CHANNEL_NAME_DEFAULT = "默认"
    const val NOTIFICATION_CHANNEL_ID_DOWNLOAD = "XhuSchedule-Download"
    const val NOTIFICATION_CHANNEL_NAME_DOWNLOAD = "下载"
    const val NOTIFICATION_CHANNEL_DESCRIPTION_DOWNLOAD = "用于应用内更新下载"
    const val NOTIFICATION_CHANNEL_ID_TOMORROW = "XhuSchedule-Tomorrow"
    const val NOTIFICATION_CHANNEL_NAME_TOMORROW = "课程提醒"
    const val NOTIFICATION_CHANNEL_DESCRIPTION_TOMORROW = "每日提醒课程或考试"

    const val NOTIFICATION_INTENT_ACTION = "notificationIntentAction"

    const val IS_FIRST_RUN = "isFirstRun"
    const val FIRST_WEEK_OF_TERM = "firstWeekOfTerm"
    const val IS_SHOW_NOT = "isShowNot"
    const val USER_IMG = "userImg"
    const val CUSTOM_BACKGROUND_IMG = "customBackgroundImg"
    const val CUSTOM_TABLE_OPACITY = "customTableOpacity"
    const val CUSTOM_TODAY_OPACITY = "customTodayOpacity"
    const val CUSTOM_TABLE_TEXT_COLOR = "customTableTextColor"
    const val CUSTOM_TODAY_TEXT_COLOR = "customTodayTextColor"
    const val CUSTOM_TEXT_SIZE = "customTextSize"
    const val CUSTOM_HEIGHT_SIZE = "customHeightSize"
    const val AUTO_CHECK_UPDATE = "autoCheckUpdate"
    const val AUTO_CHECK_LOG = "autoCheckLog"
    const val IS_ENABLE_MULTI_USER_MODE = "isEnableMultiUserMode"
    const val IS_SHOW_FAILED = "isShowFailed"
    const val IS_AUTO_SELECT = "isAutoSelect"
    const val IGNORE_UPDATE = "ignoreUpdate"
    const val SHOW_NOTICE_ID = "shownNoticeID"
    const val NOTIFICATION_SOUND = "notificationSound"
    const val NOTIFICATION_VIBRATE = "notificationVibrate"
    const val NOTIFICATION_TIME = "notificationTime"
    const val NOTIFICATION_EXACT_TIME = "notificationExactTime"
    const val NOTIFICATION_TOMORROW_ENABLE = "isNotificationTomorrowEnable"
    const val NOTIFICATION_EXAM_ENABLE = "isNotificationExamEnable"
    const val NOTIFICATION_TOMORROW_TYPE = "isNotificationTomorrowType"
}