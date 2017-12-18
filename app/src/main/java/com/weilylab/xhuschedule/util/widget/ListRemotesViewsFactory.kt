/*
 * Created by Mystery0 on 17-12-18 下午2:26.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-18 下午2:26
 */

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.graphics.Color
import android.util.Base64
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.Student
import com.weilylab.xhuschedule.util.CourseUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.XhuFileUtil
import vip.mystery0.tools.logs.Logs
import java.io.File

/**
 * Created by mystery0.
 */
class ListRemotesViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val TAG = "ListRemotesViewsFactory"
    private val showCourses = ArrayList<Course>()

    override fun onCreate() {
        val studentList = XhuFileUtil.getArrayFromFile(File(context.filesDir.absolutePath + File.separator + "data" + File.separator + "user"), Student::class.java)
        val parentFile = File(context.filesDir.absolutePath + File.separator + "caches/")
        if (!parentFile.exists())
            parentFile.mkdirs()
        val base64Name = XhuFileUtil.filterString(Base64.encodeToString(studentList[0].username.toByteArray(), Base64.DEFAULT))
        //判断是否有缓存
        val cacheResult = parentFile.listFiles().filter { it.name == base64Name }.size == 1
        if (!cacheResult) {
            Logs.i(TAG, "onCreate: cacheResult: " + cacheResult)
        }
        val oldFile = File(parentFile, base64Name)
        if (!oldFile.exists()) {
            Logs.i(TAG, "onCreate: oldFile.exists(): " + oldFile.exists())
        }
        ScheduleHelper.isCookieAvailable = true
        val todayArray = CourseUtil.getTodayCourses(XhuFileUtil.getCoursesFromFile(context, oldFile))
        showCourses.clear()
        showCourses.addAll(todayArray)
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        Logs.i(TAG, "onDataSetChanged: ")
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val course = showCourses[position]
        val remotesView = RemoteViews(context.packageName, R.layout.item_widget_today)
        remotesView.setTextViewText(R.id.course_name, course.name)
        remotesView.setTextViewText(R.id.course_teacher, course.teacher)
        remotesView.setTextViewText(R.id.course_time_location, "${course.time} at ${course.location}")
        try {
            remotesView.setInt(R.id.background, "setBackgroundColor", Color.parseColor(course.color))
        } catch (e: Exception) {
            remotesView.setInt(R.id.background, "setBackgroundColor", Color.parseColor('#' + ScheduleHelper.getRandomColor()))
        }
        return remotesView
    }

    override fun getCount(): Int {
        return showCourses.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        Logs.i(TAG, "onDestroy: ")
    }
}