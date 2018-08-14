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

package com.weilylab.xhuschedule.ui.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.constant.Constants
import com.weilylab.xhuschedule.model.Download
import vip.mystery0.tools.utils.FileTools

object DownloadNotification {
    private const val NOTIFICATION_TAG = "Download"
    private const val NOTIFICATION_ID = 1
    @SuppressLint("StaticFieldLeak")
	private lateinit var notificationBuilder: NotificationCompat.Builder

    fun notify(context: Context, fileName: String) {
        notificationBuilder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_DOWNLOAD)
                .setSound(null)
                .setVibrate(null)
                .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                .setContentTitle(context.getString(R.string.download_notification_title, fileName))
                .setOngoing(true)
                .setAutoCancel(true)
		notify(context, notificationBuilder.build())
    }

    fun updateProgress(context: Context, download: Download) {
        notificationBuilder.setProgress(100, download.progress, false)
                .setContentText(context.getString(R.string.download_notification_title_download, FileTools.formatFileSize(download.currentFileSize), FileTools.formatFileSize(download.totalFileSize)))
                .setSubText(context.getString(R.string.download_notification_text, download.progress))
		notify(context, notificationBuilder.build())
    }

    fun downloadError(context: Context) {
        notificationBuilder.setProgress(0, 0, false)
                .setContentTitle(context.getString(R.string.error_download))
                .setContentText(" ")
                .setOngoing(false)
		notify(context, notificationBuilder.build())
    }

    private fun notify(context: Context, notification: Notification) {
        val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID)
    }
}
