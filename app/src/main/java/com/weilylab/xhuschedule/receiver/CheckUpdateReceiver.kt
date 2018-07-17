/*
 * Created by Mystery0 on 4/2/18 7:35 PM.
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
 * Last modified 4/2/18 7:35 PM
 */

package com.weilylab.xhuschedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.baseClass.Version
import com.weilylab.xhuschedule.service.DownloadService
import com.weilylab.xhuschedule.util.APPActivityManager
import com.weilylab.xhuschedule.util.Constants
import com.weilylab.xhuschedule.util.Settings
import vip.mystery0.tools.utils.FileTools

class CheckUpdateReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val version = intent.getBundleExtra(Constants.INTENT_TAG_NAME_VERSION).getSerializable(Constants.INTENT_TAG_NAME_VERSION) as Version?
				?: return
		val title = context.getString(R.string.dialog_update_title, context.getString(R.string.app_version_name), version.versionName)
		val text = context.getString(R.string.dialog_update_text, version.updateLog)
		val currentActivity = APPActivityManager.appManager.currentActivity() ?: return
		val builder = AlertDialog.Builder(currentActivity)
				.setTitle(title)
				.setMessage(text)
				.setPositiveButton("${context.getString(R.string.action_download_apk)}(${FileTools.formatFileSize(version.apkSize)})", { _, _ ->
					val downloadAPKIntent = Intent(context, DownloadService::class.java)
					downloadAPKIntent.putExtra(Constants.INTENT_TAG_NAME_TYPE, Constants.DOWNLOAD_TYPE_APK)
					downloadAPKIntent.putExtra(Constants.INTENT_TAG_NAME_QINIU_PATH, version.apkQiniuPath)
					context.startService(downloadAPKIntent)
				})
		if (version.lastVersionCode == context.getString(R.string.app_version_code).toInt())
			builder.setNegativeButton("${context.getString(R.string.action_download_patch)}(${FileTools.formatFileSize(version.patchSize)})", { _, _ ->
				val downloadPatchIntent = Intent(context, DownloadService::class.java)
				downloadPatchIntent.putExtra(Constants.INTENT_TAG_NAME_TYPE, Constants.DOWNLOAD_TYPE_PATCH)
				downloadPatchIntent.putExtra(Constants.INTENT_TAG_NAME_QINIU_PATH, version.patchQiniuPath)
				context.startService(downloadPatchIntent)
			})
		if (version.must)
			builder.setOnCancelListener {
				APPActivityManager.appManager.finishAllActivity()
			}
		else
			builder.setNeutralButton(R.string.action_download_cancel, { _, _ ->
				Settings.ignoreUpdate = version.versionCode
			})
		builder.show()
	}
}
