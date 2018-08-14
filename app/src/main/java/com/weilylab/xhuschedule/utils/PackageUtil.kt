package com.weilylab.xhuschedule.utils

import com.weilylab.xhuschedule.config.APP


object PackageUtil {
	private const val qqPackage = "com.tencent.mobileqq"
	private const val timPackage = "com.tencent.tim"
	private const val wxPackage = "com.tencent.mm"
	private const val wbPackage = "com.sina.weibo"
	private const val weicoPackage = "com.weico.international"

	fun isQQApplicationAvailable(): Boolean = isApplicationAvailable(arrayListOf(qqPackage, timPackage))
	fun isWeiXinApplicationAvailable(): Boolean = isApplicationAvailable(arrayListOf(wxPackage))
	fun isWeiBoApplicationAvailable(): Boolean = isApplicationAvailable(arrayListOf(wbPackage, weicoPackage))

	/**
	 * 判断手机是否安装某个应用
	 *
	 * @param appPackageName 应用包名
	 * @return true：安装，false：未安装
	 */
	fun isApplicationAvailable(appPackageName: String): Boolean {
		val packageManager = APP.context.packageManager// 获取packageManager
		val installedPackages = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
		if (installedPackages != null)
			for (i in installedPackages.indices) {
				val pn = installedPackages[i].packageName
				if (appPackageName == pn)
					return true
			}
		return false
	}

	/**
	 * 判断手机是否安装某个应用
	 *
	 * @param arrayList 应用包名
	 * @return true：安装，false：未安装
	 */
	fun isApplicationAvailable(arrayList: ArrayList<String>): Boolean {
		val packageManager = APP.context.packageManager// 获取packageManager
		val installedPackages = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
		if (installedPackages != null)
			for (i in installedPackages.indices) {
				val pn = installedPackages[i].packageName
				if (arrayList.contains(pn))
					return true
			}
		return false
	}
}