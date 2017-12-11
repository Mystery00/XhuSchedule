/*
 * Created by Mystery0 on 17-12-7 下午9:25.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-7 下午9:25
 */

package com.weilylab.xhuschedule.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Base64
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.classes.Student
import vip.mystery0.tools.logs.Logs
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class GridRemotesViewsFactory(private val context: Context,
                              private val intent: Intent?) : RemoteViewsService.RemoteViewsFactory {
    private val TAG = "GridRemotesViewsFactory"
    private val showCourses = ArrayList<LinkedList<Course>>()

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
        val courses = XhuFileUtil.getCoursesFromFile(context, oldFile)
        if (courses.isEmpty()) {
            Logs.i(TAG, "onCreate: courses.isEmpty(): " + courses.isEmpty())
        }
        ScheduleHelper.isCookieAvailable = true
        val weekArray = CourseUtil.getWeekCourses(XhuFileUtil.getCoursesFromFile(context, oldFile))
        showCourses.clear()
//        showCourses.addAll(weekArray)
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
        val rows = position / 8 - 1
        val columns = position % 8 - 1
        if (rows == -1) {
            val remotesView = RemoteViews(context.packageName, R.layout.layout_text_view)
            if (columns == -1)
                return remotesView
            val headerArray = context.resources.getStringArray(R.array.table_header)
            remotesView.setTextViewText(R.id.textView, headerArray[columns])
            remotesView.setTextColor(R.id.textView, Color.WHITE)
            return remotesView
        }
        if (columns == -1) {
            val remotesView = RemoteViews(context.packageName, R.layout.layout_text_view)
            val navArray = context.resources.getStringArray(R.array.table_nav)
            remotesView.setTextViewText(R.id.textView, navArray[rows])
            remotesView.setTextColor(R.id.textView, Color.WHITE)
            return remotesView
        }
        val remotesViews = RemoteViews(context.packageName, R.layout.item_linear_layout)
        remotesViews.removeAllViews(R.layout.item_linear_layout)
        val linkedList = showCourses[rows * 7 + columns]
        linkedList.forEach {
            remotesViews.addView(R.id.linearLayout, addView(context, it))
        }
        return remotesViews
    }

    override fun getCount(): Int {
        return 48
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun onDestroy() {
        Logs.i(TAG, "onDestroy: ")
    }

    private fun addView(context: Context, course: Course): RemoteViews {
        val remotesViews = RemoteViews(context.packageName, R.layout.item_widget_table)
        remotesViews.setImageViewBitmap(R.id.imageView, ViewUtil.drawBackground(course))
        remotesViews.setTextViewText(R.id.textView_name, course.name)
        remotesViews.setTextViewText(R.id.textView_teacher, course.teacher)
        remotesViews.setTextViewText(R.id.textView_location, course.location)
        return remotesViews
    }
}