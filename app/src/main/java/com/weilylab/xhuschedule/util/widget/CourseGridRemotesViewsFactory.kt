/*
 * Created by Mystery0 on 17-12-21 上午3:41.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-20 下午3:39
 */

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.classes.Course
import com.weilylab.xhuschedule.util.ScheduleHelper

class CourseGridRemotesViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        return if (!WidgetHelper.hasData(WidgetHelper.showScheduleCourses)) {
            RemoteViews(context.packageName, R.layout.layout_widget_no_course)
        } else {
            val row = WidgetHelper.showScheduleCourses[position * 2]
            val remotesViews = RemoteViews(context.packageName, R.layout.item_widget_row_table)
            try {
                val navArray = context.resources.getStringArray(R.array.table_nav)
                remotesViews.setTextViewText(R.id.textView, "${navArray[position * 2]}\n\n${navArray[position * 2 + 1]}")
                row.forEachIndexed { index, list ->
                    val temp = context.resources.getIdentifier("linearLayout" + (index + 1), "id", "com.weilylab.xhuschedule")
                    remotesViews.removeAllViews(temp)
                    list.forEach {
                        remotesViews.addView(temp, addView(context, it))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            remotesViews
        }
    }

    override fun getCount(): Int {
        return if (WidgetHelper.hasData(WidgetHelper.showScheduleCourses)) 5 else 1
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun onDestroy() {
    }

    private fun addView(context: Context, course: Course): RemoteViews {
        val remotesViews = RemoteViews(context.packageName, R.layout.item_widget_table)
        try {
            remotesViews.setInt(R.id.background, "setBackgroundColor", Color.parseColor(course.color))
        } catch (e: Exception) {
            remotesViews.setInt(R.id.background, "setBackgroundColor", Color.parseColor('#' + ScheduleHelper.getRandomColor()))
        }
        remotesViews.setTextViewText(R.id.textView_name, course.name)
        remotesViews.setTextViewText(R.id.textView_teacher, course.teacher)
        remotesViews.setTextViewText(R.id.textView_location, course.location)
        return remotesViews
    }
}