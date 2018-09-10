package com.weilylab.xhuschedule.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.service.NotificationService
import java.util.*

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

	fun getDeviceID(context: Context): String {
		val deviceID = ConfigurationUtil.deviceID
		return if (deviceID == "") {
			val appVersion = "${context.getString(R.string.app_version_name)}-${context.getString(R.string.app_version_code)}"
			val systemVersion = "Android ${Build.VERSION.RELEASE}-${Build.VERSION.SDK_INT}"
			val manufacturer = Build.MANUFACTURER
			val model = Build.MODEL
			val rom = Build.DISPLAY
			val deviceIDString = "$appVersion-$systemVersion-$manufacturer-$model-$rom"
			val base64DeviceID = String(Base64.encode(deviceIDString.toByteArray(), Base64.DEFAULT))
			ConfigurationUtil.deviceID = base64DeviceID
			base64DeviceID
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
					ConfigurationUtil.updatedVersion = context.getString(R.string.app_version_code).toInt()
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
	}

	fun toHexEncoding(color: Int): String {
		var r = Integer.toHexString(Color.red(color))
		var g = Integer.toHexString(Color.green(color))
		var b = Integer.toHexString(Color.blue(color))
		val sb = StringBuffer()
		//判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
		r = if (r.length == 1) "0$r" else r
		g = if (g.length == 1) "0$g" else g
		b = if (b.length == 1) "0$b" else b
		sb.append("#")
		sb.append(r)
		sb.append(g)
		sb.append(b)
		return sb.toString()
	}

	fun getCurrentYearAndTerm() {
		if (ConfigurationUtil.isCustomYearAndTerm)
			return
		val calendar = Calendar.getInstance()
		val year = calendar.get(Calendar.YEAR)
		val month = calendar.get(Calendar.MONTH)
		if (month < Calendar.SEPTEMBER)
			ConfigurationUtil.currentYear = "${year - 1}-$year"
		else
			ConfigurationUtil.currentYear = "$year-${year + 1}"
		ConfigurationUtil.currentTerm = if (month in Calendar.MARCH until Calendar.SEPTEMBER) "2" else "1"
	}
}