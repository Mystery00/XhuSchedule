/*
 * Created by Mystery0 on 4/3/18 1:03 AM.
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
 * Last modified 4/3/18 1:03 AM
 */

package com.weilylab.xhuschedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weilylab.xhuschedule.classes.baseClass.LeanCloudPush
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.notification.PushMessageNotification
import vip.mystery0.logs.Logs

class LeanCloudPushReceiver : BroadcastReceiver() {
	private val TAG = "LeanCloudPushReceiver"

	override fun onReceive(context: Context, intent: Intent) {
//		Logs.i(TAG, "onReceive: " + intent.action)
//		val message = intent.extras.getString("com.avos.avoscloud.Data")
//		Logs.i(TAG, "onReceive: $message")
//		val leanCloudPush = ScheduleHelper.gson.fromJson(message, LeanCloudPush::class.java)
//		PushMessageNotification.notify(context, leanCloudPush)
	}
}
