package com.weilylab.xhuschedule.utils

import android.os.Environment
import com.weilylab.xhuschedule.constant.Constants
import vip.mystery0.tools.context
import java.io.File

fun getSplashImageFile(fileName: String): File? {
	if (fileName.isBlank()) return null
	return File(context().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "splash${File.separator}$fileName")
}

fun getImageFile(fileName: String): File? {
	if (fileName.isBlank()) return null
	return File(context().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image${File.separator}$fileName")
}

object FileUtil {
	const val UI_IMAGE_BACKGROUND = Constants.FILE_NAME_IMG_BACKGROUND
	const val UI_IMAGE_USER_IMG = Constants.FILE_NAME_IMG_PROFILE
}