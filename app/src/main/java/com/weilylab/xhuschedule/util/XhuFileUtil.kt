package com.weilylab.xhuschedule.util

import android.content.Context
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.Student
import java.io.*
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.util.regex.Pattern

/**
 * Created by myste.
 */
object XhuFileUtil {
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

    fun saveObjectToFile(obj: Any, file: File): Boolean {
        return try {
            if (file.exists())
                file.delete()
            if (!file.parentFile.exists())
                file.parentFile.mkdirs()
            val objectOutputStream = ObjectOutputStream(BufferedOutputStream(FileOutputStream(file)))
            objectOutputStream.writeObject(obj)
            objectOutputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCoursesFromFile(context: Context, file: File): Array<Course> {
        try {
            if (!file.exists())
                return emptyArray()
            val objectInputStream = ObjectInputStream(BufferedInputStream(FileInputStream(file)))
            @Suppress("UNCHECKED_CAST")
            val courses = objectInputStream.readObject() as Array<Course>
            val colorSharedPreference = context.getSharedPreferences("course_color", Context.MODE_PRIVATE)
            courses.forEach {
                val md5 = ScheduleHelper.getMD5(it.name)
                var savedColor = colorSharedPreference.getString(md5, "")
                if (savedColor == "") {
                    savedColor = '#' + ScheduleHelper.getRandomColor()
                    colorSharedPreference.edit().putString(md5, savedColor).apply()
                    it.color = savedColor
                } else
                    it.color = savedColor
            }
            return courses
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyArray()
        }
    }

    fun getStudentsFromFile(file: File): ArrayList<Student> {
        try {
            if (!file.exists())
                return ArrayList()
            val objectInputStream = ObjectInputStream(BufferedInputStream(FileInputStream(file)))
            @Suppress("UNCHECKED_CAST")
            return objectInputStream.readObject() as ArrayList<Student>
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

    fun copyFile(sourceFile: File, newFile: File) {

    }
}