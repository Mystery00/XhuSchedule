/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weilylab.xhuschedule.BuildConfig
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.config.APP
import com.weilylab.xhuschedule.service.NotificationService
import vip.mystery0.tools.utils.toColorString
import java.util.*

object ConfigUtil {
    private const val TAG = "ConfigUtil"
    private var lastClick = 0L

    fun isTwiceClick(): Boolean {
        val nowClick = Calendar.getInstance().timeInMillis
        if (nowClick - lastClick >= 1000) {
            lastClick = nowClick
            return false
        }
        return true
    }

    fun getDeviceID(): String {
        if (BuildConfig.DEBUG)
            return "debug"
        return Settings.Secure.getString(APP.context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun showUpdateLog(context: Context) {
        val logArray = context.resources.getStringArray(R.array.update_log)
        MaterialAlertDialogBuilder(context)
            .setTitle("${context.getString(R.string.app_name)} V${context.getString(R.string.app_version_name)} 更新日志")
            .setMessage(logArray.joinToString("\n"))
            .setPositiveButton(R.string.action_ok, null)
            .setOnDismissListener {
                ConfigurationUtil.updatedVersion =
                    context.getString(R.string.app_version_code).toInt()
            }
            .show()
    }

    fun setTrigger(context: Context, alarmManager: AlarmManager) {
        if (!ConfigurationUtil.notificationCourse && !ConfigurationUtil.notificationExam)
            return
        val alarmIntent = Intent(context, NotificationService::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager.cancel(pendingIntent)//关闭定时器
        alarmManager.set(AlarmManager.RTC_WAKEUP, CalendarUtil.getNotificationTime(), pendingIntent)
        Log.i(TAG, "setTrigger: 设置定时任务")
    }

    fun toHexEncoding(color: Int): String = color.toColorString()

    fun getCurrentYearAndTerm(startTime: Calendar) {
        if (ConfigurationUtil.isCustomYearAndTerm)
            return
        val year = startTime.get(Calendar.YEAR)
        val month = startTime.get(Calendar.MONTH)
        if (month < Calendar.JUNE) {//开始时间月份小于6月 第二学期
            ConfigurationUtil.currentYear = "${year - 1}-$year"
            ConfigurationUtil.currentTerm = "2"
        } else {//开始时间月份大于6月 第一学期
            ConfigurationUtil.currentYear = "$year-${year + 1}"
            ConfigurationUtil.currentTerm = "1"
        }
    }

    @Suppress("DEPRECATION")
    fun setStatusBar(activity: Activity) {
        activity.window.statusBarColor = Color.WHITE
        if (ConfigurationUtil.tintNavigationBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.window.navigationBarColor = Color.WHITE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        } else
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}