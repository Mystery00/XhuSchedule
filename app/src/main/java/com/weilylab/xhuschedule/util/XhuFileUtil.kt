/*
 * Created by Mystery0 on 18-1-12 下午8:52.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                  =====================================================
 *                  =                                                   =
 *                  =                       _oo0oo_                     =
 *                  =                      o8888888o                    =
 *                  =                      88" . "88                    =
 *                  =                      (| -_- |)                    =
 *                  =                      0\  =  /0                    =
 *                  =                    ___/`---'\___                  =
 *                  =                  .' \\|     |# '.                 =
 *                  =                 / \\|||  :  |||# \                =
 *                  =                / _||||| -:- |||||- \              =
 *                  =               |   | \\\  -  #/ |   |              =
 *                  =               | \_|  ''\---/''  |_/ |             =
 *                  =               \  .-\__  '-'  ___/-. /             =
 *                  =             ___'. .'  /--.--\  `. .'___           =
 *                  =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                  =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                  =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                  =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                  =                       `=---='                     =
 *                  =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                  =                                                   =
 *                  =               佛祖保佑         永无BUG              =
 *                  =                                                   =
 *                  =====================================================
 *
 * Last modified 18-1-12 下午8:51
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.weilylab.xhuschedule.classes.baseClass.Course
import com.weilylab.xhuschedule.classes.baseClass.XhuScheduleError
import vip.mystery0.tools.logs.Logs
import java.io.*
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.util.regex.Pattern

/**
 * Created by myste.
 */
object XhuFileUtil {
    private val TAG = "XhuFileUtil"

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

    fun getCoursesFromFile(context: Context, file: File): Array<Course> {
        try {
            if (!file.exists())
                return emptyArray()
            val courses = getArrayFromFile(file, Course::class.java)
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

    fun parseLog(logFile: File): XhuScheduleError {
        val bufferedReader = BufferedReader(InputStreamReader(FileInputStream(logFile)))

        var time = ""
        var appVersionName = ""
        var appVersionCode = 0
        var androidVersion = ""
        var sdk = 0
        var vendor = ""
        var model = ""
        val ex: Throwable = Exception("empty")

        var index = 0
        var temp = bufferedReader.readLine()
        while (temp != "") {
            when (index) {
                0 -> time = temp
                1 -> {
                    val tempArray = temp.substring(temp.indexOfFirst { it == ':' } + 2).split('_')
                    appVersionName = tempArray[0]
                    appVersionCode = tempArray[1].toInt()
                }
                2 -> {
                    val tempArray = temp.substring(temp.indexOfFirst { it == ':' } + 2).split('_')
                    androidVersion = tempArray[0]
                    sdk = tempArray[1].toInt()
                }
                3 -> vendor = temp
                4 -> model = temp
                else -> {
                    Logs.i(TAG, "parseLog: " + temp)
                }
            }
            index++
            temp = bufferedReader.readLine()
        }
        return XhuScheduleError(time, appVersionName, appVersionCode, androidVersion, sdk, vendor, model, ex)
    }
}