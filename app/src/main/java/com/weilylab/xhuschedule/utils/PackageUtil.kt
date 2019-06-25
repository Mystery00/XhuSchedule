package com.weilylab.xhuschedule.utils

import vip.mystery0.tools.utils.PackageTools


object PackageUtil {
	private const val qqPackage = "com.tencent.mobileqq"
	private const val timPackage = "com.tencent.tim"
	private const val wxPackage = "com.tencent.mm"
	private const val wbPackage = "com.sina.weibo"
	private const val weicoPackage = "com.weico.international"

	fun isQQApplicationAvailable(): Boolean = PackageTools.instance.isApplicationAvailable(arrayListOf(qqPackage, timPackage))
	fun isWeiXinApplicationAvailable(): Boolean = PackageTools.instance.isApplicationAvailable(arrayListOf(wxPackage))
	fun isWeiBoApplicationAvailable(): Boolean = PackageTools.instance.isApplicationAvailable(arrayListOf(wbPackage, weicoPackage))
}