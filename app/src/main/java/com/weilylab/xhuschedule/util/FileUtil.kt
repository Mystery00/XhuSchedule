package com.weilylab.xhuschedule.util

import java.io.*

/**
 * Created by myste.
 */
object FileUtil
{
	@JvmStatic
	fun saveFile(inputStream: InputStream, file: File): Boolean
	{
		try
		{
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
}