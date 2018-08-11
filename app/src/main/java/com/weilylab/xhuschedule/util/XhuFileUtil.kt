/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.*
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.util.regex.Pattern
import android.graphics.Bitmap.CompressFormat
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils


/**
 * Created by myste.
 */
object XhuFileUtil {
//	const val UI_IMAGE_BACKGROUND = Constants.FILE_NAME_IMG_BACKGROUND
//	const val UI_IMAGE_USER_IMG = Constants.FILE_NAME_IMG_PROFILE

	/**
	 * 获取存储Student信息的File对象
	 */
	fun getStudentListFile(context: Context): File {
		return File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "user")
	}

	/**
	 * 获取存储显示Student信息的File对象
	 */
	fun getShowStudentListFile(context: Context): File {
		return File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "show_user")
	}

	/**
	 * 获取存储Course信息的File对象
	 */
	fun getCourseParentFile(context: Context): File {
		return File(context.filesDir.absolutePath + File.separator + "courseList/")
	}

	/**
	 * 获取存储Exam信息的File对象
	 */
	fun getExamParentFile(context: Context): File {
		return File(context.filesDir.absolutePath + File.separator + "exam/")
	}

	/**
	 * 获取存储Score信息的File对象
	 */
	fun getScoreParentFile(context: Context): File {
		return File(context.filesDir.absolutePath + File.separator + "score/")
	}

	/**
	 * 获取存储ExpScore信息的File对象
	 */
	fun getExpScoreParentFile(context: Context): File {
		return File(context.filesDir.absolutePath + File.separator + "expScore/")
	}

	/**
	 * 获取存储Course临时信息的File对象
	 */
	fun getCourseCacheParentFile(context: Context): File {
		return File(context.filesDir.absolutePath + File.separator + "caches/")
	}

	/**
	 * 获取存储的图片文件的File对象
	 */
	fun getUIImageFile(context: Context, fileName: String): File {
		return File(File(context.filesDir, "CropImg"), fileName)
	}

	/**
	 * 获取存储的启动页图片的File对象
	 */
	fun getSplashImageFile(context: Context, objectId: String): File? {
		if (TextUtils.isEmpty(objectId))
			return null
		return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + "splash" + File.separator + objectId)
	}

	/**
	 * 获取存储四六级成绩的File对象
	 */
	fun getCETImageFile(fileName: String): File {
		return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName)
	}

	fun filterString(name: String): String {
		val regEx = "[^a-zA-Z0-9]"
		val pattern = Pattern.compile(regEx)
		val matcher = pattern.matcher(name)
		return matcher.replaceAll("").trim()
	}

	fun saveFile(inputStream: InputStream?, file: File): Boolean {
		try {
			if (!file.parentFile.exists())
				file.parentFile.mkdirs()
			if (file.exists())
				file.delete()
			val dataInputStream = DataInputStream(BufferedInputStream(inputStream))
			val dataOutputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(file)))
			val bytes = ByteArray(1024 * 1024)
			while (true) {
				val read = dataInputStream.read(bytes)
				if (read <= 0)
					break
				dataOutputStream.write(bytes, 0, read)
			}
			dataOutputStream.close()
			return true
		} catch (e: Exception) {
			e.printStackTrace()
			return false
		}
	}

	fun saveBitmapToFile(bitmap: Bitmap?, file: File): Boolean {
		return try {
			if (bitmap == null)
				return false
			if (!file.parentFile.exists())
				file.parentFile.mkdirs()
			if (file.exists())
				file.delete()
			val fileOutputStream = FileOutputStream(file)
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
			fileOutputStream.close()
			true
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

	fun saveObjectToFile(obj: Any, file: File): Boolean {
		return try {
			if (!file.parentFile.exists())
				file.parentFile.mkdirs()
			if (file.exists())
				file.delete()
			val gson = Gson()
			val fileOutputStream = FileOutputStream(file)
			fileOutputStream.write(gson.toJson(obj).toByteArray())
			fileOutputStream.close()
			true
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

	inline fun <reified T> getArrayFromFile(file: File, classOfT: Class<T>): Array<T> {
		try {
			if (!file.exists())
				return emptyArray()
			val parser = JsonParser()
			val gson = Gson()
			val fileInputStream = FileInputStream(file)
			val jsonArray = parser.parse(InputStreamReader(fileInputStream)).asJsonArray
			return Array(jsonArray.size(), { i -> gson.fromJson(jsonArray[i], classOfT) })
		} catch (e: Exception) {
			e.printStackTrace()
			return emptyArray()
		}
	}

	inline fun <reified T> getArrayListFromFile(file: File, classOfT: Class<T>): ArrayList<T> {
		try {
			if (!file.exists())
				return ArrayList()
			val parser = JsonParser()
			val gson = Gson()
			val fileInputStream = FileInputStream(file)
			val jsonArray = parser.parse(InputStreamReader(fileInputStream)).asJsonArray
			val list = ArrayList<T>()
			jsonArray.forEach {
				list.add(gson.fromJson(it, classOfT))
			}
			return list
		} catch (e: Exception) {
			e.printStackTrace()
			return ArrayList()
		}
	}

	fun getMD5(file: File): String? {
		var value: String? = null
		var fileInputStream: FileInputStream? = null
		try {
			fileInputStream = FileInputStream(file)
			val byteBuffer = fileInputStream.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
			val md5 = MessageDigest.getInstance("MD5")
			md5.update(byteBuffer)
			val bigInteger = BigInteger(1, md5.digest())
			value = bigInteger.toString(16)
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			if (null != fileInputStream) {
				try {
					fileInputStream.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

			}
		}
		return value
	}

	fun removeAllSavedPreference(context: Context, sharedPreferenceName: String) {
		val sharedPreference = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
		val keySet = sharedPreference.all.keys
		val editor = sharedPreference.edit()
		keySet.forEach {
			editor.remove(it)
		}
		editor.apply()
	}

	fun removeSavedPreference(context: Context, sharedPreferenceName: String, keys: Array<String>) {
		removeSavedPreference(context, sharedPreferenceName, keys.toList())
	}

	fun removeSavedPreference(context: Context, sharedPreferenceName: String, keys: List<String>) {
		val sharedPreference = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
		val editor = sharedPreference.edit()
		for (key in keys)
			editor.remove(key)
		editor.apply()
	}

	fun removeSavedPreference(context: Context, sharedPreferenceName: String, key: String) {
		val sharedPreference = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
		sharedPreference.edit()
				.remove(key)
				.apply()
	}

	fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray {
		val output = ByteArrayOutputStream()
		bmp.compress(CompressFormat.PNG, 100, output)
		if (needRecycle) {
			bmp.recycle()
		}
		val result = output.toByteArray()
		try {
			output.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return result
	}

	/**
	 * intent分享的uri授权
	 *
	 * @param context Context
	 * @param intent  分享的intent
	 * @param uri     分享的uri
	 */
	fun grantUriPermission(context: Context, intent: Intent, uri: Uri) {
		val list = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
		for (resolveInfo in list)
			context.grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
	}

	/**
	 * 检查对应包名的APP是否安装
	 *
	 * @param context Context
	 * @param packageName 包名
	 *
	 * @return 结果
	 */
	fun checkAPPInstall(context: Context, packageName: String): Boolean {
		val packageManager = context.packageManager
		try {
			val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
			if (packageInfo != null)
				return true
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}
		return false
	}
}