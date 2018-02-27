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
    const val WEIXIN_API_KEY = "f89cf8f34ba248c1b5b703b02d387cb7"//API Secret

    const val NOTICE_PLATFORM = "Android"

    const val FILE_NAME_IMG_BACKGROUND = "background"
    const val FILE_NAME_IMG_PROFILE = "user_img"

    const val LOG_FILE_PREFIX = "crash"
    const val LOG_FILE_SUFFIX = "txt"

    const val COURSE_TYPE_ERROR = "-1"
    const val COURSE_TYPE_ALL = "0"
    const val COURSE_TYPE_SINGLE = "1"
    const val COURSE_TYPE_DOUBLE = "2"
    const val COURSE_TYPE_NOT = "not"

    const val NOTIFICATION_SYSTEM_SOUND = "content://settings/system/notification_sound"

    const val ACTION_WIDGET_UPDATE_BROADCAST = "android.appwidget.action.APPWIDGET_UPDATE"
    const val ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"

    const val NOTIFICATION_CHANNEL_ID_DEFAULT = "XhuSchedule-Default"
    const val NOTIFICATION_CHANNEL_NAME_DEFAULT = "默认"
    const val NOTIFICATION_CHANNEL_ID_DOWNLOAD = "XhuSchedule-Download"
    const val NOTIFICATION_CHANNEL_NAME_DOWNLOAD = "下载"
    const val NOTIFICATION_CHANNEL_DESCRIPTION_DOWNLOAD = "用于应用内更新下载"
    const val NOTIFICATION_CHANNEL_ID_TOMORROW = "XhuSchedule-Tomorrow"
    const val NOTIFICATION_CHANNEL_NAME_TOMORROW = "课程提醒"
    const val NOTIFICATION_CHANNEL_DESCRIPTION_TOMORROW = "每日提醒课程或考试"

    const val NOTIFICATION_ID_FOREGROUND_ALARM = 20
    const val NOTIFICATION_ID_FOREGROUND_WIDGET = 21
    const val NOTIFICATION_ID_FOREGROUND_BOOT_COMPLETE = 22
    const val NOTIFICATION_ID_COURSE_START_INDEX = 100
    const val NOTIFICATION_ID_EXAM_START_INDEX = 200

    const val INTENT_TAG_NAME_ERROR = "error"
    const val INTENT_TAG_NAME_FILE = "file"
    const val INTENT_TAG_NAME_TAG = "TAG"
    const val INTENT_TAG_NAME_TYPE = "type"
    const val INTENT_TAG_NAME_FILE_NAME = "fileName"
    const val INTENT_TAG_NAME_QINIU_PATH = "qiniuPath"
    const val INTENT_TAG_NAME_PROFILE="profile"
    const val INTENT_TAG_NAME_LIST="list"
    const val INTENT_TAG_NAME_URL="url"

    const val DOWNLOAD_TYPE_APK="apk"
    const val DOWNLOAD_TYPE_PATCH="patch"

    const val ANIMATION_ROTATION = "rotation"
    const val ANIMATION_LEVEL = "level"
    const val ANIMATION_TRANSLATION_X = "translationX"
    const val ANIMATION_ALPHA = "alpha"

    const val SHARE_TARGET_URL = "https://www.coolapk.com/apk/com.weilylab.xhuschedule"
    const val SHARE_IMAGE_URL = "http://image.coolapk.com/apk_logo/2017/1127/ic_launcher-web-168930-o_1bvsva94q1dlcmg319lo1gvu1f5iq-uid-631231@512x512.png"

    const val SHARED_PREFERENCE_SETTINGS = "settings"
    const val SHARED_PREFERENCE_TEMP = "temp"
    const val SHARED_PREFERENCE_UPDATE_DATA = "updateData"
    const val SHARED_PREFERENCE_COURSE_COLOR = "course_color"
    const val SHARED_PREFERENCE_IDS="ids"

    const val UPDATE_VERSION = "updateVersion"

    const val SAVE_FILE = "saveFile"

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

    const val DEFAULT_TERM_START_DATE = "2017-8-4"
    const val DEFAULT_OPACITY = 154
    const val DEFAULT_COLOR_TEXT_HEADER = -1
    const val DEFAULT_COLOR_TEXT_TODAY = -11184811
    const val DEFAULT_SIZE_TEXT = 12
    const val DEFAULT_SIZE_HEIGHT = 72
    const val DEFAULT_NOTIFICATION_SOUND = NOTIFICATION_SYSTEM_SOUND
    const val DEFAULT_NOTIFICATION_TIME = "20:00"
}