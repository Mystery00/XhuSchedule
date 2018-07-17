package com.weilylab.xhuschedule.newPackage.utils

import android.content.Context
import android.net.ConnectivityManager
import com.weilylab.xhuschedule.newPackage.config.APP

object NetworkUtil {
	private val connectivityManager = APP.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

	@JvmStatic
	fun isConnectInternet(): Boolean {
		val activeNetworkInfo = connectivityManager.activeNetworkInfo
		return activeNetworkInfo != null && activeNetworkInfo.isConnected
	}
}