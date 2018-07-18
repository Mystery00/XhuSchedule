package com.weilylab.xhuschedule.newPackage.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import java.io.File

object FileUtil {
	/**
	 * 获取存储的启动页图片的File对象
	 */
	fun getSplashImageFile(context: Context, objectId: String): File? {
		if (TextUtils.isEmpty(objectId))
			return null
		return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath + File.separator + "splash" + File.separator + objectId)
	}
}