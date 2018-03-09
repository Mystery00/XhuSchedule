/*
 * Created by Mystery0 on 18-2-21 下午9:12.
 * Copyright (c) 2018. All Rights reserved.
 *
 *                    =====================================================
 *                    =                                                   =
 *                    =                       _oo0oo_                     =
 *                    =                      o8888888o                    =
 *                    =                      88" . "88                    =
 *                    =                      (| -_- |)                    =
 *                    =                      0\  =  /0                    =
 *                    =                    ___/`---'\___                  =
 *                    =                  .' \\|     |# '.                 =
 *                    =                 / \\|||  :  |||# \                =
 *                    =                / _||||| -:- |||||- \              =
 *                    =               |   | \\\  -  #/ |   |              =
 *                    =               | \_|  ''\---/''  |_/ |             =
 *                    =               \  .-\__  '-'  ___/-. /             =
 *                    =             ___'. .'  /--.--\  `. .'___           =
 *                    =          ."" '<  `.___\_<|>_/___.' >' "".         =
 *                    =         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       =
 *                    =         \  \ `_.   \_ __\ /__ _/   .-` /  /       =
 *                    =     =====`-.____`.___ \_____/___.-`___.-'=====    =
 *                    =                       `=---='                     =
 *                    =     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   =
 *                    =                                                   =
 *                    =               佛祖保佑         永无BUG              =
 *                    =                                                   =
 *                    =====================================================
 *
 * Last modified 18-2-21 下午9:11
 */

package com.weilylab.xhuschedule.util.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.weilylab.xhuschedule.R
import com.weilylab.xhuschedule.util.ColorUtil
import com.weilylab.xhuschedule.util.Settings

/**
 * Created by mystery0.
 */
class CourseListRemotesViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

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
		return if (WidgetHelper.showTodayCourses.size != 0) {
			val course = WidgetHelper.showTodayCourses[position]
			val remotesView = RemoteViews(context.packageName, R.layout.item_widget_today)
			remotesView.setTextViewText(R.id.course_name, course.name)
			remotesView.setTextViewText(R.id.course_teacher, course.teacher)
			try {
				val startTime = context.resources.getStringArray(R.array.start_time)
				val endTime = context.resources.getStringArray(R.array.end_time)
				val time = course.time.trim().split("-")
				val showTime = context.getString(R.string.course_time_format, startTime[time[0].toInt() - 1], endTime[time[1].toInt() - 1])
				remotesView.setTextViewText(R.id.course_time_location, "$showTime at ${course.location}")
			} catch (e: Exception) {
				e.printStackTrace()
				remotesView.setTextViewText(R.id.course_time_location, "${course.time} at ${course.location}")
			}
			remotesView.setInt(R.id.background, "setBackgroundColor", ColorUtil.parseColor(course.color, Settings.customTodayOpacity))
			remotesView
		} else {
			RemoteViews(context.packageName, R.layout.layout_widget_no_course)
		}
	}

	override fun getCount(): Int {
		return if (WidgetHelper.showTodayCourses.size != 0) WidgetHelper.showTodayCourses.size else 1
	}

	override fun getViewTypeCount(): Int {
		return 2
	}

	override fun onDestroy() {
	}
}