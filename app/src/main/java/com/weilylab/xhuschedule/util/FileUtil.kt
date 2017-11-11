package com.weilylab.xhuschedule.util

import java.io.*
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.util.regex.Pattern

/**
 * Created by myste.
 */
class FileUtil private constructor()
{
	companion object
	{
		private val TAG = "FileUtil"
		private var fileUtil: FileUtil? = null

		fun getInstance(): FileUtil
		{
			if (fileUtil == null)
				fileUtil = FileUtil()
			return fileUtil!!
		}
	}

	fun filterString(name: String): String
	{
		val regEx = "[^a-zA-Z0-9]"
		val pattern = Pattern.compile(regEx)
		val matcher = pattern.matcher(name)
		return matcher.replaceAll("").trim()
	}

	fun saveFile(data: String, file: File): Boolean
	{
		return saveFile(data.byteInputStream(), file)
	}

	fun saveFile(inputStream: InputStream?, file: File): Boolean
	{
		try
		{
			if (file.exists())
				file.delete()
			val dataInputStream = DataInputStream(BufferedInputStream(inputStream))
			val dataOutputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(file)))
			val bytes = ByteArray(1024 * 1024)
			while (true)
			{
				val read = dataInputStream.read(bytes)
				if (read <= 0)
					break
				dataOutputStream.write(bytes, 0, read)
			}
			dataOutputStream.close()
			return true
		}
		catch (e: Exception)
		{
			return false
		}
	}

	fun getMD5(file: File): String?
	{
		var value: String? = null
		var fileInputStream: FileInputStream? = null
		try
		{
			fileInputStream = FileInputStream(file)
			val byteBuffer = fileInputStream.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
			val md5 = MessageDigest.getInstance("MD5")
			md5.update(byteBuffer)
			val bigInteger = BigInteger(1, md5.digest())
			value = bigInteger.toString(16)
		}
		catch (e: Exception)
		{
			e.printStackTrace()
		}
		finally
		{
			if (null != fileInputStream)
			{
				try
				{
					fileInputStream.close()
				}
				catch (e: IOException)
				{
					e.printStackTrace()
				}

			}
		}
		return value
	}
}