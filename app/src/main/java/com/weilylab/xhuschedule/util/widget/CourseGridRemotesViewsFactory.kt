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
import com.weilylab.xhuschedule.classes.baseClass.Course

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
		remotesViews.setInt(R.id.background, "setBackgroundColor", course.color)
		remotesViews.setTextViewText(R.id.textView_name, course.name)
		remotesViews.setTextViewText(R.id.textView_teacher, course.teacher)
		remotesViews.setTextViewText(R.id.textView_location, course.location)
		return remotesViews
	}
}