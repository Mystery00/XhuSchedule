package com.weilylab.xhuschedule.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import com.weilylab.xhuschedule.constant.Constants
import java.io.File

object FileUtil {
	const val UI_IMAGE_BACKGROUND = Constants.FILE_NAME_IMG_BACKGROUND
	const val UI_IMAGE_USER_IMG = Constants.FILE_NAME_IMG_PROFILE
	/**
	 * 获取存储的启动页图片的File对象
	 */
	fun getSplashImageFile(context: Context, objectId: String): File? {
		if (TextUtils.isEmpty(objectId))
			return null
		return File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath}${File.separator}splash${File.separator}$objectId")
	}

	/**
	 * 获取存储的资源图片的File对象
	 */
	fun getImageFile(context: Context, fileName: String): File? {
		if (TextUtils.isEmpty(fileName))
			return null
		return File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath}${File.separator}image${File.separator}$fileName")
	}
}