/*
 * Created by Mystery0 on 17-12-18 下午2:38.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午2:38
 */

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.util.Base64
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.XhuFileUtil
import java.io.File

/**
 * Created by mystery0.
 */
object WidgetHelper {

    fun checkTodayCache(context: Context): Boolean {
        val courses = checkCache(context)
        val todayArray = CourseUtil.getTodayCourses(courses)
        return todayArray.isNotEmpty()
    }

    fun checkWeekCache(context: Context): Boolean {
        val courses = checkCache(context)
        val weekArray = CourseUtil.getWeekCourses(courses)
        return weekArray.isNotEmpty()
    }

    private fun checkCache(context: Context): Array<Course> {
        val studentList = XhuFileUtil.getArrayFromFile(File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
        val parentFile = File(context.filesDir.absolutePath + File.separator + "caches/")
        if (!parentFile.exists())
            parentFile.mkdirs()
        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(studentList[0].username.toByteArray(), Base64.DEFAULT))
        //判断是否有缓存
        val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
        if (!cacheResult)
            return emptyArray()
        val oldFile = File(parentFile, base64Name)
        if (!oldFile.exists())
            return emptyArray()
        return XhuFileUtil.getCoursesFromFile(context, oldFile)
    }
}