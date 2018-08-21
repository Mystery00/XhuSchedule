package com.weilylab.xhuschedule.utils

import android.content.Context
import android.os.Build
import android.util.Base64
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
}