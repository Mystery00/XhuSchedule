package com.weilylab.xhuschedule.utils

import vip.mystery0.tools.utils.PackageTools


object PackageUtil {
	private const val qqPackage = "com.tencent.mobileqq"
	private const val timPackage = "com.tencent.tim"
	private const val wxPackage = "com.tencent.mm"
	private const val wbPackage = "com.sina.weibo"
	private const val weicoPackage = "com.weico.international"

	fun isQQApplicationAvailable(): Boolean = PackageTools.isApplicationAvailable(arrayListOf(qqPackage, timPackage))
	fun isWeiXinApplicationAvailable(): Boolean = PackageTools.isApplicationAvailable(arrayListOf(wxPackage))
	fun isWeiBoApplicationAvailable(): Boolean = PackageTools.isApplicationAvailable(arrayListOf(wbPackage, weicoPackage))
}