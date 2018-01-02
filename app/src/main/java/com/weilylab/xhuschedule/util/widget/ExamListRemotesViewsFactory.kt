/*
 * Created by Mystery0 on 17-12-31 下午5:33.
 * Copyright (c) 2017. All Rights reserved.
 *
 * Last modified 17-12-31 下午5:33
 */

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.CalendarUtil
import com.weilylab.xhuschedule.util.ScheduleHelper
import com.weilylab.xhuschedule.util.Settings

/**
 * Created by mystery0.
 */
class ExamListRemotesViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val colorSharedPreference = context.getSharedPreferences("course_color", Context.MODE_PRIVATE)

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
        return if (WidgetHelper.showExamList.size != 0) {
            val exam = WidgetHelper.showExamList[position]
            val remotesView = RemoteViews(context.packageName, R.layout.item_widget_exam)
            remotesView.setTextViewText(R.id.exam_name, exam.name)
            remotesView.setTextViewText(R.id.exam_location_time, "${exam.time} at ${exam.location}")
            remotesView.setTextViewText(R.id.exam_no, "座位号：${exam.testno}")
            remotesView.setTextViewText(R.id.exam_days, CalendarUtil.getExamShowInfo(context, exam))
            try {
                val md5 = ScheduleHelper.getMD5(exam.name)
                var savedColor = colorSharedPreference.getString(md5, "")
                if (savedColor == "") {
                    savedColor = '#' + ScheduleHelper.getRandomColor()
                    colorSharedPreference.edit().putString(md5, savedColor).apply()
                }
                remotesView.setInt(R.id.background, "setBackgroundColor", Color.parseColor('#' + Integer.toHexString(Settings.customTodayOpacity) + savedColor.substring(1)))
            } catch (e: Exception) {
                e.printStackTrace()
                remotesView.setInt(R.id.background, "setBackgroundColor", Color.parseColor('#' + Integer.toHexString(Settings.customTodayOpacity) + ScheduleHelper.getRandomColor()))
            }
            remotesView
        } else {
            RemoteViews(context.packageName, R.layout.layout_widget_no_exam)
        }
    }

    override fun getCount(): Int {
        return if (WidgetHelper.showExamList.size != 0) WidgetHelper.showExamList.size else 1
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun onDestroy() {
    }
}