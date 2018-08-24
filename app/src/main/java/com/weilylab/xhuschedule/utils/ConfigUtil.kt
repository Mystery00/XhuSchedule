package com.weilylab.xhuschedule.utils

import android.app.job.JobScheduler
import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import com.weilylab.xhuschedule.R
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

	private const val NOTIFICATION_JOB_ID = 123

	fun setNotificationAlarm(context: Context) {
		//得到JobScheduler对象
		val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
		if (isJobPollServiceOn(context))
			scheduler.cancel(NOTIFICATION_JOB_ID)
//		else
	}

	private fun isJobPollServiceOn(context: Context): Boolean {
		val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
		var hasBeenScheduled = false
		//getAllPendingJobs得到是当前Package对应的已经安排的任务
		for (jobInfo in scheduler.allPendingJobs) {
			if (jobInfo.id == NOTIFICATION_JOB_ID) {
				hasBeenScheduled = true
				break
			}
		}
		return hasBeenScheduled
	}
}