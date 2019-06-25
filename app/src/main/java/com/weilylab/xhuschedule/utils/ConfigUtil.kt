package com.weilylab.xhuschedule.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.weilylab.xhuschedule.R
import java.util.*
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.view.View
import com.weilylab.xhuschedule.repository.local.InitLocalDataSource
import com.weilylab.xhuschedule.service.NotificationService
import vip.mystery0.logs.Logs
import vip.mystery0.tools.utils.ColorTools

object ConfigUtil {
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
		val deviceID = ConfigurationUtil.deviceID
		return if (deviceID == "") {
			UUID.randomUUID().toString()
		} else
			deviceID
	}

	fun showUpdateLog(context: Context) {
		val logArray = context.resources.getStringArray(R.array.update_log)
		val stringBuilder = StringBuilder()
		logArray.forEach {
			stringBuilder.append(it).append('\n')
		}
		AlertDialog.Builder(context)
				.setTitle("${context.getString(R.string.app_name)} V${context.getString(R.string.app_version_name)} 更新日志")
				.setMessage(stringBuilder.toString())
				.setPositiveButton(R.string.action_ok, null)
				.setOnDismissListener {
					ConfigurationUtil.updatedVersion = context.getString(R.string.app_version_code)
							.toInt()
				}
				.show()
	}

	fun setTrigger(context: Context) {
		if (!ConfigurationUtil.notificationCourse && !ConfigurationUtil.notificationExam)
			return
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val alarmIntent = Intent(context, NotificationService::class.java)
		val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			PendingIntent.getForegroundService(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		} else {
			PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		}
		alarmManager.cancel(pendingIntent)//关闭定时器
		alarmManager.set(AlarmManager.RTC_WAKEUP, CalendarUtil.getNotificationTime(), pendingIntent)
		Logs.i("setTrigger: 设置定时任务")
	}

	fun toHexEncoding(color: Int): String = ColorTools.instance.parseColorToString(color)

	fun getCurrentYearAndTerm() {
		if (ConfigurationUtil.isCustomYearAndTerm)
			return
		val startTime = InitLocalDataSource.getStartDateTime()
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

	fun setStatusBar(activity: Activity) {
		when {
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {//6.0
				activity.window.statusBarColor = Color.WHITE
				if (ConfigurationUtil.tintNavigationBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					activity.window.navigationBarColor = Color.WHITE
					activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
				} else
					activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			}
		}
	}
}