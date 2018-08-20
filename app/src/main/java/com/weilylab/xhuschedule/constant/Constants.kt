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

package com.weilylab.xhuschedule.constant

object Constants {
	const val NOTICE_PLATFORM_ANDROID = "Android"
	const val NOTICE_PLATFORM_ALL = "All"

	const val FILE_NAME_IMG_BACKGROUND = "file_name_img_background"
	const val FILE_NAME_IMG_PROFILE = "file_name_img_profile"

	const val COURSE_TYPE_ERROR = "-1"
	const val COURSE_TYPE_ALL = "0"
	const val COURSE_TYPE_SINGLE = "1"
	const val COURSE_TYPE_DOUBLE = "2"
	const val COURSE_TYPE_NOT = "not"

	const val NOTIFICATION_CHANNEL_ID_DEFAULT = "XhuSchedule-Default"
	const val NOTIFICATION_CHANNEL_NAME_DEFAULT = "默认"
	const val NOTIFICATION_CHANNEL_ID_DOWNLOAD = "XhuSchedule-Download"
	const val NOTIFICATION_CHANNEL_NAME_DOWNLOAD = "下载"
	const val NOTIFICATION_CHANNEL_DESCRIPTION_DOWNLOAD = "用于应用内更新下载"
	const val NOTIFICATION_CHANNEL_ID_TOMORROW = "XhuSchedule-Tomorrow"
	const val NOTIFICATION_CHANNEL_NAME_TOMORROW = "课程提醒"
	const val NOTIFICATION_CHANNEL_DESCRIPTION_TOMORROW = "每日提醒课程或考试"
	const val NOTIFICATION_CHANNEL_ID_PUSH = "XhuSchedule-Push"
	const val NOTIFICATION_CHANNEL_NAME_PUSH = "消息推送"
	const val NOTIFICATION_CHANNEL_DESCRIPTION_PUSH = "消息推送"

	const val INTENT_TAG_NAME_TYPE = "type"
	const val INTENT_TAG_NAME_QINIU_PATH = "qiniuPath"
	const val INTENT_TAG_NAME_APK_MD5 = "apkMD5"
	const val INTENT_TAG_NAME_PATCH_MD5 = "patchMD5"

	const val DOWNLOAD_TYPE_APK = "apk"
	const val DOWNLOAD_TYPE_PATCH = "patch"

	const val SHARE_TARGET_URL = "https://www.coolapk.com/apk/com.weilylab.xhuschedule"
	const val SHARE_IMAGE_URL = "http://image.coolapk.com/apk_logo/2018/0302/ic_launcher-168930-o_1c7i548l61b4k1p091q5l9tk15bj1b-uid-631231@192x192.png"
}