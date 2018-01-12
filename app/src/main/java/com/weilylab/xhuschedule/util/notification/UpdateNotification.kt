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

package com.weilylab.xhuschedule.util.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat

import com.weilylab.xhuschedule.R
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import com.weilylab.xhuschedule.classes.baseClass.Version
import com.weilylab.xhuschedule.service.DownloadService
import vip.mystery0.tools.fileUtil.FileUtil

object UpdateNotification {
    private val NOTIFICATION_TAG = "Update"
    private val NOTIFICATION_ID = 2

    private fun initChannelID(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = context.getString(R.string.notification_channel_id)
            val name = context.getString(R.string.notification_channel_name)
            val description = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_NONE
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.BLUE
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    fun notify(context: Context, version: Version) {
        initChannelID(context)
        val res = context.resources
        val title = res.getString(R.string.update_notification_title, context.getString(R.string.app_version_name), version.versionName)
        val content = res.getString(R.string.update_notification_content, FileUtil.FormatFileSize(version.apkSize), FileUtil.FormatFileSize(version.patchSize))

        val downloadAPKIntent = Intent(context, DownloadService::class.java)
        downloadAPKIntent.putExtra("type", "apk")
        downloadAPKIntent.putExtra("fileName", version.versionAPK)
        val pendingDownloadAPKIntent = PendingIntent.getService(context, 5, downloadAPKIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val downloadPatchIntent = Intent(context, DownloadService::class.java)
        downloadPatchIntent.putExtra("type", "patch")
        downloadPatchIntent.putExtra("fileName", version.lastVersionPatch)
        val pendingDownloadPatchIntent = PendingIntent.getService(context, 6, downloadPatchIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, "Xhu Schedule")
                .setSmallIcon(R.drawable.ic_stat_update)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingDownloadAPKIntent)
                .addAction(NotificationCompat.Action.Builder(R.drawable.ic_stat_update, context.getString(R.string.action_download_apk), pendingDownloadAPKIntent).build())
                .setAutoCancel(true)
        if (version.lastVersion == context.getString(R.string.app_version_code).toInt())
            builder.addAction(NotificationCompat.Action.Builder(R.drawable.ic_stat_update, context.getString(R.string.action_download_patch), pendingDownloadPatchIntent).build())
        notify(context, builder.build())
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
