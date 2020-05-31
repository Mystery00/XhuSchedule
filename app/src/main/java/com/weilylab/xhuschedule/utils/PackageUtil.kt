package com.weilylab.xhuschedule.utils

import vip.mystery0.tools.utils.isApplicationAvailable


object PackageUtil {
	private const val qqPackage = "com.tencent.mobileqq"
	private const val timPackage = "com.tencent.tim"
	private const val wxPackage = "com.tencent.mm"
	private const val wbPackage = "com.sina.weibo"
	private const val weicoPackage = "com.weico.international"

	fun isQQApplicationAvailable(): Boolean = isApplicationAvailable(arrayListOf(qqPackage, timPackage))
	fun isWeiXinApplicationAvailable(): Boolean = isApplicationAvailable(arrayListOf(wxPackage))
	fun isWeiBoApplicationAvailable(): Boolean = isApplicationAvailable(arrayListOf(wbPackage, weicoPackage))
}