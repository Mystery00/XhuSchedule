package com.weilylab.xhuschedule.util

import android.content.Context
import okhttp3.Cookie
import java.io.*

/**
 * Created by myste.
 */
class CookieUtil private constructor()
{
	companion object
	{
		private var cookieUtil: CookieUtil? = null

		fun getInstance(context: Context): CookieUtil
		{
			if (cookieUtil == null)
				cookieUtil = CookieUtil()
			cookieUtil!!.saveParentFile = context.cacheDir
			return cookieUtil!!
		}
	}

	lateinit var saveParentFile: File

	fun saveCookies(host: String, cookies: MutableList<Cookie>?)
	{
		val file = File(saveParentFile.absolutePath + File.separator + host)
		val objectOutputStream = ObjectOutputStream(FileOutputStream(file))
		objectOutputStream.writeObject(cookies)
	}

	@Suppress("UNCHECKED_CAST")
	fun getCookies(host: String):MutableList<Cookie>?
	{
		val file = File(saveParentFile.absolutePath + File.separator + host)
		val objectInputStream=ObjectInputStream(FileInputStream(file))
		return objectInputStream.readObject() as MutableList<Cookie>?
	}
}